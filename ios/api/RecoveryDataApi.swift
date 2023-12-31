//
//  RecoveryDataApi.swift
//  NewTonNfcCardLib
//
//  Created by Alina Alinovna on 09.09.2020.
//  Copyright © 2020 Facebook. All rights reserved.
//
import Foundation
import PromiseKit
import Foundation
import CoreNFC


@available(iOS 13.0, *)
class RecoveryDataApi: TonNfcApi {
    override init() {}
    
    func resetRecoveryData() {
        print("Start card operation: resetRecoveryData")
        executeTonWalletOperation(apdu: TonWalletAppletApduCommands.RESET_RECOVERY_DATA_APDU)
    }

    func isRecoveryDataSet() {
        print("Start card operation: isRecoveryDataSet")
        apduRunner.setCardOperation(cardOperation: { () in
            self.apduRunner.sendTonWalletAppletApdu(apduCommand:
                    TonWalletAppletApduCommands.IS_RECOVERY_DATA_SET_APDU)
                .then{(response : Data)  -> Promise<String> in
                    if (response.count != TonWalletAppletConstants.IS_RECOVERY_DATA_SET_LE) {
                        throw ResponsesConstants.ERROR_IS_RECOVERY_DATA_SET_RESPONSE_LEN_INCORRECT
                    }
                    let boolResponse = response.bytes[0] == 0 ? ResponsesConstants.FALSE_MSG : ResponsesConstants.TRUE_MSG
                    return self.makeFinalPromise(result : boolResponse)
                }
        })
        apduRunner.startScan()
    }
    
    func getRecoveryDataHash() {
        print("Start card operation: getRecoveryDataHash")
        apduRunner.setCardOperation(cardOperation: { () in
            self.apduRunner.sendTonWalletAppletApdu(apduCommand:
                    TonWalletAppletApduCommands.GET_RECOVERY_DATA_HASH_APDU)
                .then{(response : Data)  -> Promise<String> in
                    if (response.count != TonWalletAppletConstants.SHA_HASH_SIZE) {
                        throw ResponsesConstants.ERROR_MSG_RECOVERY_DATA_HASH_RESPONSE_LEN_INCORRECT
                    }
                    return self.makeFinalPromise(result : response.hexEncodedString())
                }
        })
        apduRunner.startScan()
    }
    
    func getRecoveryDataLen() {
        print("Start card operation: getRecoveryDataLen")
        apduRunner.setCardOperation(cardOperation: { () in
            self.apduRunner.sendTonWalletAppletApdu(apduCommand:
                    TonWalletAppletApduCommands.GET_RECOVERY_DATA_LEN_APDU)
                .then{(response : Data)  -> Promise<String> in
                    if (response.count != TonWalletAppletConstants.GET_RECOVERY_DATA_LEN_LE) {
                        throw ResponsesConstants.ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_LEN_INCORRECT
                    }
                    let len = ByteArrayAndHexHelper.makeShort(src: response.bytes, srcOff: 0)
                    if (len <= 0 || len > TonWalletAppletConstants.RECOVERY_DATA_MAX_SIZE) {
                        throw ResponsesConstants.ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_INCORRECT
                    }
            
                    return self.makeFinalPromise(result : String(len))
                }
        })
        apduRunner.startScan()
    }
    
    func addRecoveryData(recoveryData: String) {
        print("Start card operation: addRecoveryData")
        guard dataVerifier.checkRecoveryDataSize(recoveryData: recoveryData) &&
                dataVerifier.checkRecoveryDataFormat(recoveryData: recoveryData) else {
            return
        }
        print("Got recoveryData:" + recoveryData)
        let recoveryDataBytes = ByteArrayAndHexHelper.hexStrToUInt8Array(hexStr: recoveryData)
        let recoveryDataSize = UInt16(recoveryDataBytes.count)
        print("recoveryDataSize = " + String(recoveryDataSize))
        apduRunner.setCardOperation(cardOperation: { () in
            self.addRecoveryData(recoveryDataBytes: recoveryDataBytes)
                .then{(response : Data)  -> Promise<String> in
                    return self.makeFinalPromise(result : ResponsesConstants.DONE_MSG)
                }
        })
        apduRunner.startScan()
    }
    
    private func addRecoveryData(recoveryDataBytes: [UInt8]) -> Promise<Data> {
        let numberOfFullPackets = recoveryDataBytes.count / TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE
        print("numberOfFullPackets = " + String(numberOfFullPackets))
        var sendRecoveryDataPromise = self.apduRunner.sendApdu(apduCommand:
                                                            TonWalletAppletApduCommands.SELECT_TON_WALLET_APPLET_APDU)
        
        for index in 0..<numberOfFullPackets {
            var newSendRecoveryDataPromise = sendRecoveryDataPromise.then{(response : Data) -> Promise<Data> in
                print("#packet " + String(index))
                let chunk = recoveryDataBytes[range: index * TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE..<(index + 1) * TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE]
                return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand: try TonWalletAppletApduCommands.getAddRecoveryDataPartApdu(p1: index == 0 ? 0x00 : 0x01, data : chunk))
            }
            sendRecoveryDataPromise = newSendRecoveryDataPromise
        }
        
        let tailLen = recoveryDataBytes.count % TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE
        if tailLen > 0 {
            sendRecoveryDataPromise = sendRecoveryDataPromise.then{(response : Data) -> Promise<Data> in
                let chunk = recoveryDataBytes[range: numberOfFullPackets * TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE..<numberOfFullPackets * TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE + tailLen]
                return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand: try TonWalletAppletApduCommands.getAddRecoveryDataPartApdu(p1: numberOfFullPackets == 0 ? 0x00 : 0x01, data : chunk))
            }
        }
        
        return sendRecoveryDataPromise.then{(response : Data) -> Promise<Data> in
            self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand: try TonWalletAppletApduCommands.getAddRecoveryDataPartApdu(p1: 0x02, data: Data(_ : recoveryDataBytes).hash().bytes))
        }
    }
    
    func getRecoveryData() {
        apduRunner.setCardOperation(cardOperation: { () in
            self.apduRunner.sendTonWalletAppletApdu(apduCommand: TonWalletAppletApduCommands.GET_RECOVERY_DATA_LEN_APDU)
                .then{(response : Data) -> Promise<String> in
                    let recoveryDataLen = ByteArrayAndHexHelper.makeShort(src : response.bytes, srcOff : 0)
                    print("Recovery data len = " + String(recoveryDataLen))
                    return self.getRecoveryData(recoveryDataLen : recoveryDataLen)
                }
        })
        apduRunner.startScan()
    }
    
    private func getRecoveryData(recoveryDataLen : Int) -> Promise<String> {
        let numberOfFullPackets = recoveryDataLen / TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE
        print("numberOfFullPackets = " + String(numberOfFullPackets))
        var getRecoveryDataPromise = self.apduRunner.sendApdu(apduCommand:
                                                                TonWalletAppletApduCommands.SELECT_TON_WALLET_APPLET_APDU)
        var startPos: UInt16 = 0
        var recoveryData = Data(_ : [])
        
        for index in 0..<numberOfFullPackets {
            var newGetRecoveryDataPromise = getRecoveryDataPromise.then{(response : Data) -> Promise<Data> in
                print("packet# " + String(index))
                return self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand: try TonWalletAppletApduCommands.getGetRecoveryDataPartApdu(startPositionBytes: [UInt8(startPos >> 8), UInt8(startPos)], le: TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE))
            }
            .then{(chunk : Data) -> Promise<Data> in
                //TODO: check chunk size, do the same for Android
                startPos = startPos + UInt16(TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE)
                recoveryData.append(chunk)
                return Promise { promise in promise.fulfill(Data(_ : []))}
            }
            getRecoveryDataPromise = newGetRecoveryDataPromise
        }
        
        let tailLen = recoveryDataLen % TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE
        if tailLen > 0 {
            getRecoveryDataPromise = getRecoveryDataPromise.then{(response : Data) -> Promise<Data> in
                return  self.apduRunner.sendAppletApduAndCheckAppletState(apduCommand: try TonWalletAppletApduCommands.getGetRecoveryDataPartApdu(startPositionBytes: [UInt8(startPos >> 8), UInt8(startPos)], le: tailLen))
            }
            .then{(chunk : Data) -> Promise<Data> in
                //TODO: check chunk size, do the same for Android
                recoveryData.append(chunk)
                return Promise<Data> { promise in promise.fulfill(recoveryData)}
            }
        }
        return getRecoveryDataPromise.then{(recoveryData : Data) -> Promise<String> in
            return self.makeFinalPromise(result : recoveryData .hexEncodedString())
        }
    }
}
