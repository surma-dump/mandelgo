package main

import (
	"image/png"
	"os"
	"testing"
)

func BenchmarkRender(b *testing.B) {
	b.StopTimer()
	m := &MandelParams{
		Width:      500,
		Height:     500,
		Scale:      0.005,
		ColorScale: 10,
		Origin:     complex(-0.5, 0),
	}
	f, e := os.Create("/dev/null")
	if e != nil {
		panic(e)
	}
	defer f.Close()
	b.StartTimer()
	for i := 0; i < b.N; i++ {
		img := m.Render()
		e = png.Encode(f, img)
		if e != nil {
			panic(e)
		}
	}
	b.StopTimer()
}
