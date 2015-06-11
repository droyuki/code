inputClose <- function(Close){

  #Close為讀取收盤價向量

  while(length(Close)<=90){
  }
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
  

  
  乖離訊號1=((Close[length(Close)]-SMA13[length(SMA13)])/SMA13[length(SMA13)])<(-0.0003315)
  乖離訊號2=((Close-SMA21[length(SMA21)])/SMA21[length(SMA21)])<(-0.0010215)
  乖離訊號3=((Close-SMA55[length(SMA55)])/SMA55[length(SMA55)])<(-0.0005193)
  強勢訊號1=((Close-SMA55[length(SMA55)])/SMA55[length(SMA55)])>0.00014248
  強勢訊號2=(SMA13[length(SMA13)]>SMA21[length(SMA21)])&(SMA21[length(SMA21)]>SMA55[length(SMA55)])
  強勢訊號3=(SMA21[length(SMA21)]>SMA55[length(SMA55)])&(SMA55[length(SMA55)]>SMA89[length(SMA89)])
  整理訊號1=(abs((SMA13[length(SMA13)]-SMA21[length(SMA21)])/SMA21[length(SMA21)])<0.00080704)&(abs((SMA21[length(SMA21)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])<0.00080704)
  整理訊號2=(abs((SMA21[length(SMA21)]-SMA55[length(SMA55)])/SMA55[length(SMA55)])<0.00117728)&(abs((SMA55[length(SMA55)]-SMA89[length(SMA89)])/SMA89[length(SMA89)])<0.00117728)
  signalsVector = c('乖離訊號1'= 乖離訊號1, '乖離訊號2' = 乖離訊號2, '乖離訊號3' = 乖離訊號3, '整理訊號1' = 整理訊號1, '整理訊號2' = 整理訊號2, '強勢訊號1' = 強勢訊號1, '強勢訊號2' = 強勢訊號2, '強勢訊號3'= 強勢訊號3)
  return signalsVector
}
