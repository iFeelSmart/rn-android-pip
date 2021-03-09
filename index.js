import { NativeModules } from 'react-native'
import PipEventManager from './PipEventManager'

const { RNAndroidPip } = NativeModules

export default RNAndroidPip
export { PipEventManager }
