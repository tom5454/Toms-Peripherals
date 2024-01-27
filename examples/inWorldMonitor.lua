-- Display your terminal on an advanced monitor
-- The keyboard is on top of the computer
-- Place this code into your startup.lua file
local monID = "monitor_0"
local kb = peripheral.wrap("top")
kb.setFireNativeEvents(true)
shell.run("monitor "..monID.." clear")
shell.run("monitor "..monID.." multishell")
