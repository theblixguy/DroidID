//
//  ChangePasswordViewController.swift
//  DroidIDBeta
//
//  Created by Suyash Srijan on 20/03/2016.
//  Copyright © 2016 Suyash Srijan. All rights reserved.
//

import Cocoa
import Locksmith

class ChangePasswordViewController: NSViewController {
    
    @IBOutlet weak var passwordField: NSSecureTextField!
    
    override func viewDidAppear() {
        super.viewDidAppear()
        let styleMask: NSWindowStyleMask = [NSWindowStyleMask.miniaturizable, NSWindowStyleMask.resizable, NSWindowStyleMask.fullSizeContentView]
        self.view.window!.styleMask.insert(styleMask)
        self.view.window!.titlebarAppearsTransparent = true
        self.view.window?.title = ""
    }
    
    @IBAction func changePwd(_ sender: Any) {
        if passwordField.stringValue.isEmpty {
            let alert:NSAlert = NSAlert();
            alert.messageText = "Uh oh";
            alert.informativeText = "You did not enter a password";
            alert.runModal();
        } else {
            try! Locksmith.updateData(data: ["MacPwd": passwordField.stringValue], forUserAccount: "MacPwd")
            self.view.window?.close()
        }
        
    }
}
