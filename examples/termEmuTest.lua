-- Emulates the CC terminal on the Bitmap monitors.
-- Requires the 'term_emu.lua' program from the examples repo

-- GPU peripheral id
local gpuID = "tm_gpu_0"
-- Keyboard peripheral id
local kbID = "tm_keyboard_0"
-- Terminal character size upscaling
local scale = 1

-- Run the program on the emulated terminal
local function runProgram()
    shell.run("multishell")
end

local emu = require "term_emu"
local font = require "cc_term_font"
local gpu = peripheral.wrap(gpuID)
gpu.refreshSize()
gpu.setSize(64)
gpu.fill(0)
local function doSync()
    gpu.sync()
end
sleep(0.25)-- Wait for refreshSize
local sx, sy = gpu.getSize()
local tw = math.floor(sx / 6 / scale)
local th = math.floor(sy / 9 / scale)
if tw < 5 or th < 3 then
    error("Your current monitor is too small for terminal emulation with scale: "..scale)
end
font.upload(gpu)
local rd = emu.create(gpu, doSync, tw, th, true, scale, "unicode_page_e0")
rd.auto_update()

-- Updates the blinking cursor
local function updateCursor() 
    local i = 0
    while true do
        i = i + 1
        rd.tick(i % 8 == 0)
        sleep(0.1)
    end
end

local function pipeEvents()
    while true do
        local event, per, x, y, btn = os.pullEvent()

        if event == "tm_monitor_mouse_click" and per == gpuID then
            os.queueEvent("mouse_click", btn, rd.mapPixel(x, y))
        elseif event == "tm_monitor_touch" and per == gpuID then
            os.queueEvent("mouse_click", 1, rd.mapPixel(x, y))
            os.queueEvent("mouse_up", 1, rd.mapPixel(x, y))
        elseif event == "tm_monitor_mouse_up" and per == gpuID then
            os.queueEvent("mouse_up", btn, rd.mapPixel(x, y))
        elseif event == "tm_monitor_mouse_drag" and per == gpuID then
            os.queueEvent("mouse_drag", btn, rd.mapPixel(x, y))
        elseif event == "tm_monitor_mouse_scroll" and per == gpuID then
            os.queueEvent("mouse_scroll", btn, rd.mapPixel(x, y))
        elseif event == "tm_keyboard_key" and per == kbID then
            os.queueEvent("key", x, y)
        elseif event == "tm_keyboard_key_up" and per == kbID then
            os.queueEvent("key_up", x, y)
        elseif event == "tm_keyboard_char" and per == kbID then
            os.queueEvent("char", x, y)
        end
   end
end

local oldTerm = term.redirect(rd.term)

parallel.waitForAny(updateCursor, pipeEvents, runProgram)

term.redirect(oldTerm)
