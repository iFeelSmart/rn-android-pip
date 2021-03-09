import { NativeEventEmitter, NativeModules } from 'react-native'

export default class PipEventManager {
    constructor() {
        this.emitter = null
    }

    addListerner = (cb) => {
        this.emitter = new NativeEventEmitter(NativeModules.DeviceEventEmitter)
        this.emitter.addListener('onPipModeChanged', cb)
    }

    removeListerner = (cb) => {
        this.emitter.removeListener('onPipModeChanged', cb)
        this.emitter = null
    }
}
