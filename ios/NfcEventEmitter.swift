
//
 //  NfcEventEmitter.swift
 //  react-native-new-ton-nfc-card-lib
 //
 //  Created by Alina Alinovna on 09.12.2020.
 //

 import Foundation

 @objc(NfcEventEmitter)
 class NfcEventEmitter: RCTEventEmitter {
     static let NFC_TAG_CONNECTED_EVENT:String = "nfcTagConnected"

     public static var emitter:RCTEventEmitter!

     override init() {
         super.init()
         NfcEventEmitter.emitter = self
     }

     override func supportedEvents() -> [String]! {
         return [NfcEventEmitter.NFC_TAG_CONNECTED_EVENT]
     }
 }
