//
 //  NfcEventEmitter.m
 //  react-native-new-ton-nfc-card-lib
 //
 //  Created by Alina Alinovna on 09.12.2020.
 //

 #import <Foundation/Foundation.h>
 #import <React/RCTBridgeModule.h>
 #import <React/RCTEventEmitter.h>

 @interface RCT_EXTERN_MODULE(NfcEventEmitter, RCTEventEmitter)

 RCT_EXTERN_METHOD(supportedEvents)

 @end
