# SpeedoMeter

you can edit source code based on your requirment please look at speedometerview.java it easy to customize needle,guage and all other stuff

<image src=/speedo.gif
 width=225 height=400>
 
**note:** use only square view

### how to use
```xml
 <com.jignesh13.speedometer.SpeedoMeterView
        android:id="@+id/speedometerview"
        android:layout_width="250dp"
        android:layout_height="250dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.453"
        app:layout_constraintStart_toStartOf="parent"
        app:backimage="@android:color/black"
        app:needlecolor="#fff"
        app:removeborder="false"
        app:linecolor="#fff"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.079" />

```

```java
      SpeedoMeterView speedoMeterView=findViewById(R.id.speedometerview);
      speedoMeterView.setSpeed(60,true);//speed set 0 to 140
      speedoMeterView.setisborder(!speedoMeterView.isborder());//add or remove border
      speedoMeterView.setLinecolor(Color.WHITE);//set line and textcolor
      speedoMeterView.setNeedlecolor(Color.WHITE);//set speed needle color
      speedoMeterView.setbackImageResource(R.color.colorAccent);//you set image resource or color resource
      
```
 
 
##  Developer
  jignesh khunt
  (jigneshkhunt13@gmail.com)
  
##  License

Copyright 2019 jignesh khunt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
