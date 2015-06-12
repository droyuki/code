inputClose <- function(Close){

  
  SMA13=rep(0,100)
  SMA21=rep(0,100)
  SMA55=rep(0,100)
  SMA89=rep(0,100)
  for(i in 13:length(Close)){
    k=c()
    for(j in 1:13){
      k[j]=Close[i-(j-1)]
    }
    SMA13[i]=mean(k,na.rm = T)
  }
  
  for(i in 21:length(Close)){
    k=c()
    for(j in 1:21){
      k[j]=Close[i-(j-1)]
    }
    SMA21[i]=mean(k,na.rm = T)
  }
  
  for(i in 55:length(Close)){
    k=c()
    for(j in 1:55){
      k[j]=Close[i-(j-1)]
    }
    SMA55[i]=mean(k,na.rm = T)
  }
  
  for(i in 89:length(Close)){
    k=c()
    for(j in 1:89){
      k[j]=Close[i-(j-1)]
    }
    SMA89[i]=mean(k,na.rm = T)
  }
  
  
  
  biasOne=((Close[length(Close)]-SMA13[length(SMA13)])/SMA13[length(SMA13)])<(-0.0003315)
  biasTwo=((Close[length(Close)]-SMA21[length(SMA21)])/SMA21[length(SMA21)])<(-0.0010215)
  biasThree=((Close[length(Close)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])<(-0.0005193)
  strongOne=((Close[length(Close)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])>0.00014248
  strongTwo=(SMA13[length(SMA13)]>SMA21[length(SMA21)])&(SMA21[length(SMA21)]>SMA55[length(SMA55)])
  strongThree=(SMA21[length(SMA21)]>SMA55[length(SMA55)])&(SMA55[length(SMA55)]>SMA89[length(SMA89)])
  tOne=(abs((SMA13[length(SMA13)]-SMA21[length(SMA21)])/SMA21[length(SMA21)])<0.00080704)&(abs((SMA21[length(SMA21)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])<0.00080704)
  tTwo=(abs((SMA21[length(SMA21)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])<0.00117728)&(abs((SMA55[length(SMA55)]-SMA89[length(SMA89)])/SMA89[length(SMA89)])<0.00117728)
  signalsVector = c(biasOne, biasTwo, biasThree, tOne, tTwo, strongOne, strongTwo, strongThree)
  
  yyy=1.7654*(!signalsVector[1])+1.1588*(signalsVector[1])+ 1.4022*(signalsVector[4]) + 0.3526*(signalsVector[5])-0.5267* (signalsVector[8])+ 1.0773*(signalsVector[2])+ 0.2146* (signalsVector[7])-1.7729*(signalsVector[4]*signalsVector[5])+ 2.1481*(signalsVector[4]*signalsVector[7])+2.1656*(signalsVector[1]*signalsVector[4])-2.2605*(signalsVector[4]*signalsVector[2])+ 0.7606*(signalsVector[1]*signalsVector[8])-1.2580*(signalsVector[5]*signalsVector[7])-1.1721*(signalsVector[4]*signalsVector[8])
  return(biasTwo)
}