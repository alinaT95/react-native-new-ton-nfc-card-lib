
import {NativeModules} from 'react-native';
const {NfcCardModule} = NativeModules;

export default class NfcCardModuleWrapper {

  /* Coin manager functions */

  static async getMaxPinTries() {
    return await NfcCardModule.getMaxPinTries();
  }

  static async getSeVersion() {
    return await NfcCardModule.getSeVersion();
  }

  static async getCsn(){
    return await NfcCardModule.getCsn();
  }

  static async getDeviceLabel(){
    return await NfcCardModule.getDeviceLabel();
  }

  static async setDeviceLabel(label){
    return await NfcCardModule.setDeviceLabel(label);
  }

  static async getRemainingPinTries(){
    return await NfcCardModule.getRemainingPinTries();
  }

  static async getRootKeyStatus(){
    return await NfcCardModule.getRootKeyStatus();
  }

  static async getRootKeyStatus(){
    return await NfcCardModule.getRootKeyStatus();
  }

  static async getAvailableMemory(){
    return await NfcCardModule.getAvailableMemory();
  }

  static async getAppsList(){
    return await NfcCardModule.getAppsList();
  }

  static async generateSeed(pin){
    return await NfcCardModule.generateSeed(pin);
  }

  static async resetWallet(){
    return await NfcCardModule.resetWallet();
  }

  static async changePin(oldPin, newPin){
    return await NfcCardModule.changePin(oldPin, newPin);
  }

  /* Commands to maintain keys for hmac */

  static async selectKeyForHmac(serialNumber){
    return await NfcCardModule.selectKeyForHmac(serialNumber);
  }

  static async createKeyForHmac(authenticationPassword, commonSecret, serialNumber){
    return await NfcCardModule.createKeyForHmac(authenticationPassword, commonSecret, serialNumber);
  }

  static async getCurrentSerialNumber(){
    return await NfcCardModule.getCurrentSerialNumber();
  }

  static async getAllSerialNumbers(){
    return await NfcCardModule.getAllSerialNumbers();
  }

  static async isKeyForHmacExist(serialNumber){
    return await NfcCardModule.isKeyForHmacExist(serialNumber);
  }

  static async deleteKeyForHmac(serialNumber){
    return await NfcCardModule.deleteKeyForHmac(serialNumber);
  }

  /* Card activation commands (TonWalletApplet) */

  static async turnOnWallet(newPin, authenticationPassword, commonSecret, initialVector){
    return await NfcCardModule.turnOnWallet(newPin, authenticationPassword, commonSecret, initialVector);
  }

  static async getHashOfEncryptedPassword(){
    return await NfcCardModule.getHashOfEncryptedPassword();
  }

  static async getHashOfEncryptedCommonSecret(){
    return await NfcCardModule.getHashOfEncryptedCommonSecret();
  }

  /* Common stuff (TonWalletApplet)  */

  static async getTonAppletState(){
    return await NfcCardModule.getTonAppletState();
  }

  static async getSerialNumber(){
    return await NfcCardModule.getSerialNumber();
  }

  /* Recovery data stuff (TonWalletApplet)  */

  static async addRecoveryData(recoveryData){
    return await NfcCardModule.addRecoveryData(recoveryData);
  }

  static async getRecoveryData(){
    return await NfcCardModule.getRecoveryData();
  }

  static async getRecoveryDataHash(){
    return await NfcCardModule.getRecoveryDataHash();
  }

  static async getRecoveryDataLen(){
    return await NfcCardModule.getRecoveryDataLen();
  }

  static async isRecoveryDataSet(){
    return await NfcCardModule.isRecoveryDataSet();
  }

  static async resetRecoveryData(){
    return await NfcCardModule.resetRecoveryData();
  }

  /* Ed25519 stuff (TonWalletApplet)  */

  static async verifyPin(pin){
    return await NfcCardModule.verifyPin(pin);
  }

  static async getPublicKey(hdIndex){
    return await NfcCardModule.getPublicKey(hdIndex);
  }

  static async signForDefaultHdPath(dataForSigning, pin){
    return await NfcCardModule.verifyPinAndSignForDefaultHdPath(dataForSigning, pin);
  }

  static async sign(dataForSigning, hdIndex, pin){
    return await NfcCardModule.verifyPinAndSign(dataForSigning, hdIndex, pin);
  }

  static async getPublicKeyForDefaultPath(){
    return await NfcCardModule.getPublicKeyForDefaultPath();
  }

  /* Keychain commands */

  static async resetKeyChain(){
    return await NfcCardModule.resetKeyChain();
  }

  static async getKeyChainDataAboutAllKeys(){
    return await NfcCardModule.getKeyChainDataAboutAllKeys();
  }

  static async getKeyChainInfo(){
    return await NfcCardModule.getKeyChainInfo();
  }

  static async getNumberOfKeys(){
    return await NfcCardModule.getNumberOfKeys();
  }

  static async getOccupiedStorageSize(){
    return await NfcCardModule.getOccupiedStorageSize();
  }

  static async getFreeStorageSize(){
    return await NfcCardModule.getFreeStorageSize();
  }

  static async getKeyFromKeyChain(keyHmac){
    return await NfcCardModule.getKeyFromKeyChain(keyHmac);
  }

  static async addKeyIntoKeyChain(newKey){
    return await NfcCardModule.addKeyIntoKeyChain(newKey);
  }

  static async deleteKeyFromKeyChain(keyHmac){
    return await NfcCardModule.deleteKeyFromKeyChain(keyHmac);
  }

  static async finishDeleteKeyFromKeyChainAfterInterruption(keyHmac){
    return await NfcCardModule.finishDeleteKeyFromKeyChainAfterInterruption(keyHmac);
  }

  static async changeKeyInKeyChain(newKey, oldKeyHmac){
    return await NfcCardModule.changeKeyInKeyChain(newKey, oldKeyHmac);
  }

  static async getIndexAndLenOfKeyInKeyChain(keyHmac){
    return await NfcCardModule.getIndexAndLenOfKeyInKeyChain(keyHmac);
  }

  static async checkAvailableVolForNewKey(keySize){
    return await NfcCardModule.checkAvailableVolForNewKey(keySize);
  }

  static async checkKeyHmacConsistency(keyHmac){
    return await NfcCardModule.checkKeyHmacConsistency(keyHmac);
  }
}