-- 3D rotating cube
local tris = {
    -- SOUTH
    { 0.0, 0.0, 0.0,    0.0, 1.0, 0.0,    1.0, 1.0, 0.0 },
    { 0.0, 0.0, 0.0,    1.0, 1.0, 0.0,    1.0, 0.0, 0.0 },
    -- NORTH                                                     
    { 1.0, 0.0, 1.0,    1.0, 1.0, 1.0,    0.0, 1.0, 1.0 },
    { 1.0, 0.0, 1.0,    0.0, 1.0, 1.0,    0.0, 0.0, 1.0 },
    -- EAST                                                      
    { 1.0, 0.0, 0.0,    1.0, 1.0, 0.0,    1.0, 1.0, 1.0 },
    { 1.0, 0.0, 0.0,    1.0, 1.0, 1.0,    1.0, 0.0, 1.0 },
    -- WEST                                                      
    { 0.0, 0.0, 1.0,    0.0, 1.0, 1.0,    0.0, 1.0, 0.0 },
    { 0.0, 0.0, 1.0,    0.0, 1.0, 0.0,    0.0, 0.0, 0.0 },
    -- TOP                                                       
    { 0.0, 1.0, 0.0,    0.0, 1.0, 1.0,    1.0, 1.0, 1.0 },
    { 0.0, 1.0, 0.0,    1.0, 1.0, 1.0,    1.0, 1.0, 0.0 },
    -- BOTTOM                                                    
    { 1.0, 0.0, 1.0,    0.0, 0.0, 1.0,    0.0, 0.0, 0.0 },
    { 1.0, 0.0, 1.0,    0.0, 0.0, 0.0,    1.0, 0.0, 0.0 },
};
local gpu = peripheral.wrap("tm_gpu_0")
gpu.sync()
gpu.setSize(64)
local gl = gpu.createWindow3D(1, 1, 768, 320)
gl.glFrustum(90, 0.1, 1000)
gl.glDirLight(0, 0, -1)
local rot = 0
while true do
    gl.clear()
    gl.glDisable(0xDE1)
    gl.glTranslate(0, 1, 3)
    gl.glRotate(rot, 0, 1, 0)
    gl.glRotate(rot, 0, 0, 1)
    rot = rot + 3
    gl.glBegin()
    --gl.glColor(0, 0, 255)
    for k,v in pairs(tris) do
        local ci = math.floor((k - 1) / 2)
    	gl.glVertex(v[1], v[2], v[3])
    	local cv = 255
    	if ci % 2 == 0 then
    		cv = 127
    	end
    	if math.floor(ci / 2) == 0 then
    		gl.glColor(cv, 0, 0)
    	elseif math.floor(ci / 2) == 1 then
    		gl.glColor(0, cv, 0)
    	else
    		gl.glColor(0, 0, cv)
    	end
    	gl.glVertex(v[4], v[5], v[6])
    	gl.glVertex(v[7], v[8], v[9])
    end
    gl.glEnd()
    gl.render()
    gl.sync()
    gpu.sync()
    sleep(0.01)
end
