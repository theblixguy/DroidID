//
//  ViewController.swift
//  DroidIDBeta
//
//  Created by Suyash Srijan on 19/03/2016.
//  Copyright © 2016 Suyash Srijan. All rights reserved.
//

import Cocoa
import Firebase
import Locksmith
import Quartz

class ViewController: NSViewController {
    
    var statusBar = NSStatusBar.system()
    var statusItem : NSStatusItem = NSStatusItem()
    var menuItem1 : NSMenuItem = NSMenuItem()
    var menuItem2 : NSMenuItem = NSMenuItem()
    var menuItem3 : NSMenuItem = NSMenuItem()
    var mainMenu = NSMenu()
    
    var isLocked = false
    
    @IBOutlet weak var authCodeField: NSTextField!
    @IBOutlet weak var passwordField: NSSecureTextFieldCell!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func quitApp(_ sender : AnyObject?) {
        exit(0)
    }
    
    func changePassword(_ sender: AnyObject?) {
        performSegue(withIdentifier: "changePassword", sender: self)
    }
    
    func unlinkMac(_ sender : AnyObject?) {
        try! Locksmith.deleteDataForUserAccount(userAccount: "MacPwd")
        UserDefaults.standard.removePersistentDomain(forName: Bundle.main.bundleIdentifier!)
        restartApp()
    }
    
    func restartApp() {
        let task = Process()
        let script = Bundle.main.path(forResource: "RestartApp", ofType: "scpt")
        task.launchPath = "/usr/bin/osascript"
        task.arguments = [script!]
        task.launch()
    }
    
    func initAppMenu() {
        menuItem1.title = "Quit"
        menuItem1.action = #selector(ViewController.quitApp(_:))
        menuItem1.target = self
        menuItem1.keyEquivalent = "Q"
        menuItem1.isEnabled = true
        mainMenu.addItem(menuItem1)
        
        menuItem2.title = "Change password"
        menuItem2.action = #selector(ViewController.changePassword(_:))
        menuItem2.target = self
        menuItem2.keyEquivalent = "C"
        menuItem2.isEnabled = true
        mainMenu.addItem(menuItem2)
        
        menuItem3.title = "Unlink this Mac"
        menuItem3.action = #selector(ViewController.unlinkMac(_:))
        menuItem3.target = self
        menuItem3.keyEquivalent = "U"
        menuItem3.isEnabled = true
        mainMenu.addItem(menuItem3)
        
        let menuIcon = NSApp.applicationIconImage
        menuIcon?.isTemplate = true
        menuIcon?.size = NSSize(width: 16, height: 16)
        statusItem = statusBar.statusItem(withLength: CGFloat(NSVariableStatusItemLength))
        statusItem.image = menuIcon
        statusItem.menu = mainMenu
        
    }
    
    @IBAction func connectApp(_ sender: AnyObject) {
        
        var authCodeEntered = false
        var passwordEntered = false
        
        if authCodeField.stringValue.isEmpty {
            let alert:NSAlert = NSAlert();
            alert.messageText = "Uh oh";
            alert.informativeText = "You did not enter the authentication code";
            alert.runModal();
        }
        
        if authCodeField.stringValue.characters.count != 6 {
            let alert:NSAlert = NSAlert();
            alert.messageText = "Uh oh";
            alert.informativeText = "Authentication code has to be 6 characters long";
            alert.runModal();
            
        } else if authCodeField.stringValue.characters.count == 6 {
            authCodeEntered = true
        }
        
        if passwordField.stringValue.isEmpty {
            let alert:NSAlert = NSAlert();
            alert.messageText = "Uh oh";
            alert.informativeText = "You did not enter your Mac's password";
            alert.runModal();
            
        } else {
            passwordEntered = true
        }
        
        if authCodeEntered && passwordEntered {
            let defaults = UserDefaults.standard
            defaults.set(authCodeField.stringValue.uppercased(), forKey: "AuthCode")
            defaults.synchronize()
            try! Locksmith.updateData(data: ["MacPwd": passwordField.stringValue], forUserAccount: "MacPwd")
            let signupRef = Firebase(url:"https://path_to_firebase_instance" + authCodeField.stringValue.uppercased() + "/signedUp")
            signupRef?.setValue("true")
            performSegue(withIdentifier: "ready", sender: self)
            self.view.window?.orderOut(self)
        }
    }
    
    
    @IBAction func showPwdInfo(_ sender: AnyObject) {
        let alert:NSAlert = NSAlert();
        alert.messageText = "Why does this app require my Mac's password?";
        alert.informativeText = "To unlock your Mac, DroidID requires your password to unlock your Mac. Don't worry though, your password is in safe hands because DroidID stores your password securely in Keychain. ";
        alert.runModal();
    }
    
    override func viewDidAppear() {
        super.viewDidAppear()
        self.view.window!.styleMask.insert(NSWindowStyleMask.fullSizeContentView)
        self.view.window!.titlebarAppearsTransparent = true
        
        let defaults = UserDefaults.standard
        let token = defaults.string(forKey: "AuthCode")
        
        if token != nil {
            DispatchQueue.main.async(execute: {
                self.initAppMenu()
                let dictionary = Locksmith.loadDataForUserAccount(userAccount: "MacPwd")
                let pwd = dictionary!["MacPwd"]
                self.view.window?.orderOut(self)
                let myRootRef = Firebase(url:"https://path_to_firebase_instance" + token! + "/unlockMac")
                
                let center: DistributedNotificationCenter = DistributedNotificationCenter.default()
                center.addObserver(self, selector: #selector(ViewController.screenLocked), name: NSNotification.Name(rawValue: "com.apple.screenIsLocked"), object: nil)
                center.addObserver(self, selector: #selector(ViewController.screenUnlocked), name: NSNotification.Name(rawValue: "com.apple.screenIsUnlocked"), object: nil)
                
                
                myRootRef?.observe(.value, with: {
                    snapshot in
                    if (snapshot?.value! as! NSObject) as! Bool == true {
                        if self.isLocked == true {
                            self.wakeDisplayFromSleep()
                            let task = Process()
                            //let pipe = NSPipe()
                            let script = Bundle.main.path(forResource: "UnlockMac", ofType: "scpt")
                            task.launchPath = "/usr/bin/osascript"
                            task.arguments = [script!, pwd! as! String]
                            //task.standardOutput = pipe
                            task.launch()
                            myRootRef?.setValue(false)
                            //let data = pipe.fileHandleForReading.readDataToEndOfFile()
                            //let output: String = String(data: data, encoding: NSUTF8StringEncoding)!
                            //print(output)
                        } else {
                            myRootRef?.setValue(false)
                        }
                    }
                })
                
            })
        }
        
    }
    
    func screenLocked() {
        isLocked = true
    }
    
    func screenUnlocked() {
        isLocked = false
    }
    
    func wakeDisplayFromSleep() {
        let entry = IORegistryEntryFromPath(kIOMasterPortDefault, "IOService:/IOResources/IODisplayWrangler");IORegistryEntrySetCFProperty(entry, "IORequestIdle" as CFString!, kCFBooleanFalse);
        IOObjectRelease(entry);
    }
    
    override func viewWillAppear() {
        super.viewWillAppear()
    }
    
}

