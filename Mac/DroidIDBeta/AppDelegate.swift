//
//  AppDelegate.swift
//  DroidIDBeta
//
//  Created by Suyash Srijan on 19/03/2016.
//  Copyright © 2016 Suyash Srijan. All rights reserved.
//

import Cocoa
import ServiceManagement

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {
    
    func applicationDidFinishLaunching(_ aNotification: Notification) {
        
        let defaults = UserDefaults.standard
        let defaultValueForAuthCode = ["AuthCode" : ""]
        defaults.register(defaults: defaultValueForAuthCode)
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        // Insert code here to tear down your application
    }


}

