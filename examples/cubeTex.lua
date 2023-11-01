-- 3D rotating cube with the stone texture
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
}

local uvs = {
    -- SOUTH
    { 0.0, 0.0,    0.0, 1.0,    1.0, 1.0 },
    { 0.0, 0.0,    1.0, 1.0,    1.0, 0.0 },
    -- NORTH
    { 1.0, 0.0,    0.0, 0.0,    0.0, 1.0 },
    { 1.0, 0.0,    0.0, 1.0,    1.0, 1.0 },
    -- EAST
    { 1.0, 0.0,    0.0, 0.0,    0.0, 1.0 },
    { 1.0, 0.0,    0.0, 1.0,    1.0, 1.0 },
    -- WEST
    { 1.0, 0.0,    0.0, 0.0,    0.0, 1.0 },
    { 1.0, 0.0,    0.0, 1.0,    1.0, 1.0 },
    -- TOP
    { 0.0, 1.0,    0.0, 0.0,    1.0, 0.0 },
    { 0.0, 1.0,    1.0, 0.0,    1.0, 1.0 },
    -- BOTTOM
    { 0.0, 0.0,    0.0, 1.0,    1.0, 1.0 },
    { 0.0, 0.0,    1.0, 1.0,    1.0, 0.0 },
}

local gpu = peripheral.wrap("tm_gpu_0")
gpu.sync()
gpu.setSize(64)
local gl = gpu.createWindow3D(1, 1, 768, 320)
gl.glFrustum(90, 0.1, 1000)
gl.glDirLight(0, 0, -1)

local of = io.open("stone.png", "rb")
local b = of._handle.read(1)
local imgBin = {}
while b do
  imgBin[#imgBin + 1] = ("<I1"):unpack(b);
  b = of._handle.read(1)
end
local image = gpu.decodeImage(table.unpack(imgBin))
local texID = gl.glGenTextures()
gl.glBindTexture(texID)
gl.glTexImage(image.ref())
gl.glEnable(3553)

local rot = 0
while true do
    gl.clear()
    gl.glTranslate(0, 1, 3)
    gl.glRotate(rot, 0, 1, 0)
    gl.glRotate(rot, 0, 0, 1)
    gl.glColor(255, 255, 255)
    rot = rot + 3
    gl.glBegin()
    for k,v in pairs(tris) do
    	local u = uvs[k]
    	gl.glVertex(v[1], v[2], v[3])
    	gl.glTexCoord(u[1], u[2])
    	gl.glVertex(v[4], v[5], v[6])
    	gl.glTexCoord(u[3], u[4])
    	gl.glVertex(v[7], v[8], v[9])
    	gl.glTexCoord(u[5], u[6])
    end
    gl.glEnd()
    gl.render()
    gl.sync()
    gpu.sync()
    sleep(0.01)
end
