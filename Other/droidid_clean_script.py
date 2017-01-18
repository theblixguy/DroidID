# This script is used to remove users who were allocated an auth code but didnt sign up properly
# Written by: Suyash Srijan

import json

json_obj = json.load(open('droidid.json'))
for key in json_obj["users"].keys():
    if(not json_obj["users"][key]["signedUp"]):
    	print "Deleting ->", key
    	del(json_obj["users"][key])

open("updated-droidid.json", "w").write(json.dumps(json_obj, sort_keys=True, indent=2, separators=(',', ': '))
)