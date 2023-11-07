# react-native-new-ton-nfc-card-lib

Here there is native  Android and iOS responsible for work with Ton NFC security cards.

## Installation

You can install into your project from this github repo:

npm install git+https://github.com/tonlabs/react-native-new-ton-nfc-card-lib --save

You need additional step for iOS platform. Go to ios subfolder of your react native project and run command

pod install

For iOS you need additionally set up your project to work with NFC. Here there is an instruction https://www.notion.so/tonlabs/Instruction-to-prepare-npm-package-containing-native-code-and-include-into-React-native-project-upd-b3612387478644f7a969f19daedb4582

## Usage


import NfcHandler from 'react-native-new-ton-nfc-card-lib'

// ...

const result = await NfcHandler.NfcCardModule.getMaxPinTries();



