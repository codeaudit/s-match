@echo off
setlocal
call setCP.cmd

java -Xmx256M -Xms256M -Dlog4j.configuration=..\conf\log4j.properties -cp %CP% it.unitn.disi.smatch.MatchManager wntoflat -prop=..\conf\SMatchDefault.properties

endlocal