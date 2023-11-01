-- Draws a pixel on the screen where the player clicks
local gpu = peripheral.wrap("tm_gpu_0")
gpu.refreshSize()
gpu.fill()
gpu.sync()
while true do
    local e, p, x, y, sneak = os.pullEvent()
    if e == "key_up" and p == keys.t then
        break
    end
    print(e..":   "..tostring(x)..", "..tostring(y).." "..tostring(sneak)) 
    if e == "tm_monitor_touch" then
    	if sneak then
    		gpu.filledRectangle(x, y, 1, 1, 0x666666)
    	else
    		gpu.filledRectangle(x - 1, y - 1, 3, 3, 0xFFFFFF)
    	end
        gpu.sync()
    end
end
