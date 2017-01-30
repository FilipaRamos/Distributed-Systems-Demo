# Distributed-Systems-Demo

  Labs developed for the Distributed Systems curricular unit. Project for the same curricular unit. It consists of a server application which comunicates with other peers in a LAN. The objective is to create a backup system involving all the computers in the LAN. Communication between peers is acheived through udp multicast.
  
  To successfully run the program the following steps must be performed in the exact same order:
  
    1. Run the server with the desired arguments.
    2. Run the TestApp with the desired arguments.
    3. If there is the need to send a new command repeat number 2.
    
  Arguments for the server:
  
    1. IP addres for the control channel
    2. port for the control channel
    3. IP addres for the backup channel
    4. port for the backup channel
    5. IP addres for the restore channel
    6. port for the restore channel
    7. true or false indicating whether the backup protocol should be the enhanced one or not
    8. true or false indicating whether the reclaim protocol should be the enhanced one or not
    9. port to communicate with the TestApp
    
  Arguments for the testApp:
  
    1. As described in the interface specification
