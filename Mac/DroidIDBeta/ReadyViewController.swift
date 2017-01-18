//
//  ReadyViewController.swift
//  DroidIDBeta
//
//  Created by Suyash Srijan on 19/03/2016.
//  Copyright © 2016 Suyash Srijan. All rights reserved.
//

import Cocoa

class ReadyViewController: NSViewController {
    
    override func viewDidAppear() {
        super.viewDidAppear()
        let styleMask: NSWindowStyleMask = [NSWindowStyleMask.miniaturizable, NSWindowStyleMask.resizable, NSWindowStyleMask.fullSizeContentView]
        self.view.window!.styleMask.insert(styleMask)
        self.view.window!.titlebarAppearsTransparent = true
        self.view.window?.title = ""
    }
    
    
    @IBAction func closeWindow(_ sender: AnyObject) {
            let task = Process()
            let script = Bundle.main.path(forResource: "RestartApp", ofType: "scpt")
            task.launchPath = "/usr/bin/osascript"
            task.arguments = [script!]
            task.launch()
    }
}


