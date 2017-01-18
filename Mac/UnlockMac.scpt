on run argv
set pword to (item 1 of argv)
tell application "System Events"
stop current screen saver
keystroke pword
delay 0.5
keystroke return
end tell
end run