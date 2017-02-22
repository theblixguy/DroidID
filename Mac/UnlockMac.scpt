on run argv
set pword to (item 1 of argv)
tell application "System Events"
stop current screen saver
repeat with i from 1 to count characters of pword
keystroke (character i of pword)
end repeat
delay 0.5
keystroke return
end tell
end run
