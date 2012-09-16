package main

import (
	"flag"
	"fmt"
	hsva "github.com/kortschak/biogo/graphics/color"
	"image"
	"image/png"
	"log"
	"math"
	"net/http"
	"strconv"
	"strings"
	"sync"
	"time"
)

const ITERATION_LIMIT = 100

var (
	httpAddr = flag.String("http", "localhost:8080", "Address to bind the webserver to")
	helpFlag = flag.Bool("help", false, "Show this help")
)

func main() {
	flag.Parse()

	if *helpFlag {
		flag.PrintDefaults()
		return
	}

	http.HandleFunc("/render_image", handler)
	log.Printf("Starting server...")
	log.Fatal(http.ListenAndServe(*httpAddr, nil))
}

func handler(w http.ResponseWriter, r *http.Request) {
	e := r.ParseForm()
	if e != nil {
		http.Error(w, fmt.Sprintf("Invalid query string: %s", e), 400)
		return
	}

	p, e := parseMandelParams(r)
	if e != nil {
		http.Error(w, fmt.Sprintf("Invalid query string: %s", e), 400)
		return
	}
	log.Printf("Rendering %#v", p)
	t := time.Now()
	img := p.Render()
	log.Printf("Elapsed time: %s", time.Since(t))
	w.Header().Set("Content-Type", "image/png")
	e = png.Encode(w, img)
	if e != nil {
		log.Printf("Could not encode image: %s", e)
	}
}

type MandelParams struct {
	// Dimension of the image in pixels
	Width  int
	Height int
	// Corresponding coordinates of the center of the picutre
	Origin complex128
	// Units per pixel
	Scale      float64
	ColorScale float64
}

func (m *MandelParams) Render() image.Image {
	img := image.NewRGBA(image.Rect(0, 0, int(m.Width), int(m.Height)))
	xremap := genRemap(0, float64(m.Width), -m.Scale*float64(m.Width)/2, m.Scale*float64(m.Width)/2)
	yremap := genRemap(0, float64(m.Height), -m.Scale*float64(m.Height)/2, m.Scale*float64(m.Height)/2)
	var wg sync.WaitGroup
	wg.Add(m.Height)
	for y := 0; y < m.Height; y++ {
		go func(y int) {
			for x := 0; x < m.Width; x++ {
				x_ := real(m.Origin) + xremap(float64(x))
				y_ := imag(m.Origin) + yremap(float64(y))
				z := complex(x_, y_)
				c := z
				i := int64(0)
				for i < ITERATION_LIMIT && cplxlensqrd(z) <= 4.0 {
					z = z*z + c
					i++
				}
				var col hsva.HSVA
				if cplxlensqrd(z) <= 4.0 {
					col = hsva.HSVA{0, 0, 0, 1.0}
				} else {
					col = hsva.HSVA{(float64(i) + (2.0-math.Sqrt(cplxlensqrd(z)))/2.0) / ITERATION_LIMIT * 360.0 * m.ColorScale, 1.0, 1.0, 1.0}
				}
				for col.H > 360.0 {
					col.H -= 360.0
				}
				img.Set(x, y, col)
			}
			wg.Done()
		}(y)
	}
	wg.Wait()
	return img
}

func ParseComplex(s string) (c complex128, err error) {
	real, imag := 0.0, 0.0
	fields := strings.Split(s, ",")
	if len(fields) >= 2 {
		imag, err = strconv.ParseFloat(fields[1], 64)
		if err != nil {
			return
		}
	}
	real, err = strconv.ParseFloat(fields[0], 64)
	return complex(real, imag), err
}

func parseMandelParams(r *http.Request) (m *MandelParams, e error) {
	defer func() {
		if x := recover(); x != nil {
			m, e = nil, fmt.Errorf("Invalid value: %s", x)
			return
		}
	}()
	must := func(v interface{}, e error) interface{} {
		if e != nil {
			panic(e)
		}
		return v
	}
	return &MandelParams{
		Width:      int(must(strconv.ParseInt(r.Form.Get("width"), 10, 64)).(int64)),
		Height:     int(must(strconv.ParseInt(r.Form.Get("height"), 10, 64)).(int64)),
		Scale:      must(strconv.ParseFloat(r.Form.Get("scale"), 64)).(float64),
		ColorScale: must(strconv.ParseFloat(r.Form.Get("colorscale"), 64)).(float64),
		Origin:     must(ParseComplex(r.Form.Get("origin"))).(complex128),
	}, nil
}

// Dirty helper
func genRemap(oldLow, oldHigh, newLow, newHigh float64) func(float64) float64 {
	return func(val float64) float64 {
		return (val-oldLow)/(oldHigh-oldLow)*(newHigh-newLow) + newLow
	}
}

func cplxlensqrd(c complex128) float64 {
	return real(c)*real(c) + imag(c)*imag(c)
}
