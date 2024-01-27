-- Draws a pixel on the screen where the player clicks
-- Shows the cursor when using the Keyboard peripheral
local gpu = peripheral.wrap("tm_gpu_0")
gpu.refreshSize()
gpu.setSize(16)
gpu.fill()
gpu.sync()
local w, h = gpu.getSize()
local drawW = gpu.createWindow(1, 1, w, h)
local w = 3
while true do
    local e, p, x, y, s = os.pullEvent()
    if e == "key_up" and p == keys.t then
        break
    end
    print(e..":   "..tostring(x)..", "..tostring(y).." "..tostring(s))
    if e == "tm_monitor_touch" then
    	if s then
    		drawW.filledRectangle(x, y, 1, 1, 0x666666)
    	else
    		drawW.filledRectangle(math.max(1, x - math.floor(w/2)), math.max(1, y - math.floor(w/2)), w, w, 0xFFFFFF)
    	end
    	drawW.sync()
        gpu.sync()
    elseif e == "tm_monitor_mouse_click" or e == "tm_monitor_mouse_drag" then
    	if s == 2 then
    		drawW.filledRectangle(x, y, 1, 1, 0x666666)
    	else
    		drawW.filledRectangle(math.max(1, x - math.floor(w/2)), math.max(1, y - math.floor(w/2)), w, w, 0xFFFFFF)
    	end
        drawW.sync()
        gpu.sync()
    elseif e == "tm_monitor_mouse_scroll" then
    	w = math.max(1, w - s * 2)
    	
    	gpu.fill()
    	drawW.sync()
    	gpu.filledRectangle(math.max(1, x - math.floor(w/2)), math.max(1, y - math.floor(w/2)), w, w, 0xFF0000)
    	gpu.sync()
    elseif e == "tm_monitor_mouse_move" then
    	gpu.fill()
    	drawW.sync()
    	gpu.filledRectangle(math.max(1, x - math.floor(w/2)), math.max(1, y - math.floor(w/2)), w, w, 0xFF0000)
    	gpu.sync()
    end
end
