# HyperRing Core Library

## How to use
### Add it in your root build.gradle at the end of repositories:
`dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}`

### Add the dependency
`dependencies {
    implementation 'com.github.HyperRingSW:HyperRingSDKCore:TAG'
}`

## HyperRingNFC
### Init
`HyperRingNFC.initializeHyperRingNFC(context)`

### Then if startPolling
`HyperRingNFC.startNFCTagPolling(
    context as Activity, 
    onDiscovered = :: onDiscovered
)`

### stopPolling
`HyperRingNFC.stopNFCTagPolling(context as Activity)`

### HyperRingTag
#### onDiscovered return HyperRingTag Tag data

### onDiscovered get Tagged data
`private fun onDiscovered(hyperRingTag: HyperRingTag) : HyperRingTag`
#### You can use HyperRingNFC.write, HyperRingNFC.read Functions in onDiscovered

### When using HyperRingNFC read, write functions.  HyperRingData is used.
#### (override encrypt, decrypt functions)

### Writing Example Code1 - AESHRData is Example HyperRingData with AES algo.
#### AESHRData inheritance HyperRingData
`HyperRingNFC.write(
    uiState.value.targetWriteId, 
    hyperRingTag, 
    **AESHRData**.createData(uiState.value.dataTagId?:10, "Jenny Doe")`

### Writing Example Code2 - JWTHRData is Example HyperRingData with JWT.
#### JWTHRData inheritance HyperRingData
`HyperRingNFC.write(
    uiState.value.targetWriteId, 
    hyperRingTag, 
    **JWTHRData**.createData(10, "John Doe", MainActivity.jwtKey)
)`

### Reading Example
`val readTag: HyperRingTag? = HyperRingNFC.read(uiState.value.targetReadId, hyperRingTag)`

## HyperRingMFA

### Init
#### mfaData is for MFA data 
#### mfaData parameter should not be empty
`HyperRingMFA.initializeHyperRingMFA(mfaData= mfaData.toList())`

### Init Example Code
`val mfaData: MutableList<HyperRingMFAChallengeInterface> = mutableListOf()
// AES Type
mfaData.add(**AESMFAChallengeData**(10, "dIW6SbrLx+dfb2ckLIMwDOScxw/4RggwXMPnrFSZikA\u003d\n", null))
// JWT Type
mfaData.add(**JWTMFAChallengeData**(15, "John Doe", null, MainActivity.jwtKey))
HyperRingMFA.initializeHyperRingMFA(mfaData= mfaData.toList())`
#### AESMFAChallengeData, JWTMFAChallengeData is CustomMFAChallengeData
#### It is based on HyperRingMFAChallengeInterface
#### (override encrypt, decrypt, challenge functions)

### requestHyperRingMFAAuthentication
`HyperRingMFA.requestHyperRingMFAAuthentication(
activity = MainActivity.mainActivity!!,
onNFCDiscovered = ::onDiscovered,
autoDismiss = autoDismiss)`
#### onNFCDiscovered get Tagged data from Base HyperRing Polling UI
`fun onDiscovered(dialog: Dialog?, response: MFAChallengeResponse?) {
    HyperRingMFA.verifyHyperRingMFAAuthentication(response)
}`
### requestHyperRingMFAAuthentication
#### Verify response from onDiscovered
