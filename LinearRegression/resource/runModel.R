signalsVector = c(乖離訊號1, 乖離訊號2, 乖離訊號3, 整理訊號1, 整理訊號2, 強勢訊號1, 強勢訊號2, 強勢訊號3)
runModel <- function(signalsVector){
  yyy=1.7654*(!signalsVector[1])+1.1588*(signalsVector[1])+ 1.4022*(signalsVector[4]) + 0.3526*(signalsVector[5])-0.5267* (signalsVector[8])+ 1.0773*(signalsVector[2])+ 0.2146* (signalsVector[7])-1.7729*(signalsVector[4]*signalsVector[5])+ 2.1481*(signalsVector[4]*signalsVector[7])+2.1656*(signalsVector[1]*signalsVector[4])-2.2605*(signalsVector[4]*signalsVector[2])+ 0.7606*(signalsVector[1]*signalsVector[8])-1.2580*(signalsVector[5]*signalsVector[7])-1.1721*(signalsVector[4]*signalsVector[8])
  return yyy
}