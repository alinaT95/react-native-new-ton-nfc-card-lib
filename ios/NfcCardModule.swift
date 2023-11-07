//
//  NfcCardModule.swift
//  NewTonNfcCardLib
//
//  Created by Alina Alinovna on 03.09.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import CoreNFC
import PromiseKit
import CryptoKit

@available(iOS 13.0, *)
@objc(NfcCardModule)
class NfcCardModule: NSObject {
    let hmacHelper = HmacHelper.getInstance()
    
    var cardCoinManagerNfcApi: CardCoinManagerNfcApi = CardCoinManagerNfcApi()
    var cardCryptoNfcApi: CardCryptoNfcApi = CardCryptoNfcApi()
    var cardActivationNfcApi: CardActivationNfcApi = CardActivationNfcApi()
    var cardKeyChainNfcApi: CardKeyChainNfcApi = CardKeyChainNfcApi()
    var recoveryDataApi: RecoveryDataApi = RecoveryDataApi()
    
    @objc
    func isNfcSupported(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        resolve(NFCTagReaderSession.readingAvailable ? ResponsesConstants.TRUE_MSG : ResponsesConstants.FALSE_MSG);
    }
    
    /* Coinmanager stuff*/
    @objc
    func getSeVersion(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getSeVersion()
    }
    
    @objc
    func getCsn(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getCsn()
    }
    
    @objc
    func getDeviceLabel(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getDeviceLabel()
    }
    
    @objc
    func setDeviceLabel(_ label: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.setDeviceLabel(label: label)
    }
    
    @objc
    func getMaxPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getMaxPinTries()
    }
    
    @objc
    func getRemainingPinTries(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getRemainingPinTries()
    }
    
    @objc
    func getRootKeyStatus(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getRootKeyStatus()
    }
    
    @objc
    func getAvailableMemory(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getAvailableMemory()
    }
    
    @objc
    func getAppsList(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.getAppsList()
    }
    
    @objc
    func generateSeed(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.generateSeed(pin: pin)
    }
    
    @objc
    func resetWallet(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.resetWallet()
    }
    
    @objc
    func changePin(_ oldPin: String, newPin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCoinManagerNfcApi.changePin(oldPin: oldPin, newPin: newPin)
    }
    
    /* Ton wallet applet common stuff */
    @objc
    func getSault(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.getSault()
    }
    
    @objc
    func getSerialNumber(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.getSerialNumber()
    }
    
    @objc
    func getTonAppletState(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.getTonAppletState()
    }
    
    /* Commands to maintain keys for hmac */
    
   
    
    @objc
    func selectKeyForHmac(_ serialNumber: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.selectKeyForHmacAndReturnIntoCallback(serialNumber : serialNumber)
    }
    
    @objc
    func createKeyForHmac(_ password: String, commonSecret : String, serialNumber: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.createKeyForHmac(password : password, commonSecret : commonSecret, serialNumber: serialNumber)
    }
    
    @objc
    func getCurrentSerialNumber(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.getCurrentSerialNumberAndPutIntoCallback()
    }
    
    @objc
    func getAllSerialNumbers(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.getAllSerialNumbers()
    }
    
    @objc
    func isKeyForHmacExist(_ serialNumber: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.isKeyForHmacExistAndReturnIntoCallback(serialNumber: serialNumber)
    }
    
    @objc
    func deleteKeyForHmac(_ serialNumber: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.deleteKeyForHmac(serialNumber: serialNumber)
    }
    
    /* Ton wallet applet card activation related stuff */
    
    
    @objc
    func turnOnWallet(_ newPin: String, password: String, commonSecret : String, initialVector : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.turnOnWallet(newPin: newPin, password: password, commonSecret: commonSecret, initialVector: initialVector)
    }
    
    @objc
    func verifyPassword(_ password: String, initialVector: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.verifyPassword(password: password, initialVector: initialVector)
    }
    
    @objc
    func getHashOfEncryptedCommonSecret(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.getHashOfEncryptedCommonSecret()
    }
    
    @objc
    func getHashOfEncryptedPassword(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardActivationNfcApi.getHashOfEncryptedPassword()
    }
    
    /* Ton wallet applet recovery data stuff*/
    
    @objc
    func resetRecoveryData(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.resetRecoveryData()
    }
    
    @objc
    func getRecoveryDataHash(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.getRecoveryDataHash()
    }
    
    @objc
    func getRecoveryDataLen(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.getRecoveryDataLen()
    }
    
    @objc
    func isRecoveryDataSet(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.isRecoveryDataSet()
    }
    
    @objc
    func getRecoveryData(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.getRecoveryData()
    }
    
    @objc
    func addRecoveryData(_ recoveryData: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        recoveryDataApi.addRecoveryData(recoveryData: recoveryData)
    }
    
    
    
    
    /* Ton wallet applet ed25519 related stuff*/
    @objc
    func getPublicKeyForDefaultPath(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.getPublicKeyForDefaultPath()
    }
    

    @objc
    func getPublicKey(_ hdIndex: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.getPublicKey(hdIndex: hdIndex)
    }
    
    @objc
    func verifyPin(_ pin: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.verifyPin(pin: pin)
    }
    
    @objc
    func verifyPinAndSignForDefaultHdPath(_ data: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.signForDefaultHdPath(data: data, pin: pin)
    }
    
    @objc
    func verifyPinAndSign(_ data: String, hdIndex: String, pin : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardCryptoNfcApi.sign(data: data, hdIndex: hdIndex, pin: pin)
    }
    
    /* Ton wallet applet keychain related stuff */
    @objc
    func getKeyChainDataAboutAllKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getKeyChainDataAboutAllKeys()
    }
    
    @objc
    func getKeyChainInfo(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getKeyChainInfo()
    }
    
    @objc
    func finishDeleteKeyFromKeyChainAfterInterruption(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.finishDeleteKeyFromKeyChainAfterInterruption(keyMac : keyMac)
    }
    
    @objc
    func deleteKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.deleteKeyFromKeyChain(keyMac : keyMac)
    }
    
    @objc
    func getKeyFromKeyChain(_ keyMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getKeyFromKeyChain(keyMac : keyMac)
    }
    
    @objc
    func addKeyIntoKeyChain(_ newKey: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.addKeyIntoKeyChain(newKey : newKey)
    }
    
    @objc
    func changeKeyInKeyChain(_ newKey: String, oldKeyHMac : String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.changeKeyInKeyChain(newKey : newKey, oldKeyHMac: oldKeyHMac)
    }
    
    
    @objc
    func resetKeyChain(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.resetKeyChain()
    }
    
    @objc
    func getDeleteKeyChunkNumOfPackets(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getDeleteKeyChunkNumOfPackets()
    }
    
    @objc
    func getDeleteKeyRecordNumOfPackets(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getDeleteKeyRecordNumOfPackets()
    }
    
    @objc
    func getNumberOfKeys(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getNumberOfKeys()
    }
    
    @objc
    func getOccupiedStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getOccupiedStorageSize()
    }
    
    @objc
    func getFreeStorageSize(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getFreeStorageSize()
    }
    
    @objc
    func checkKeyHmacConsistency(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.checkKeyHmacConsistency(keyHmac: keyHmac)
    }
    
    @objc
    func checkAvailableVolForNewKey(_ keySize: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.checkAvailableVolForNewKey(keySize: keySize)
    }
    
    @objc
    func getIndexAndLenOfKeyInKeyChain(_ keyHmac: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getIndexAndLenOfKeyInKeyChain(keyHmac: keyHmac)
    }
    
    @objc
    func getHmac(_ index: String, resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
        NfcCallback.callback.set(resolve: CallbackHelper.createResolver(resolve: resolve), reject: CallbackHelper.createRejecter(reject: reject))
        cardKeyChainNfcApi.getHmac(index: index)
    }
}
