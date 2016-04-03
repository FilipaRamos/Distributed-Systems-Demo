# SDIS-labs

  Labs developed for the Distributed Systems curricular unit. Project for the same curricular unit. It consists of a server application which comunicates with other peers in a LAN. The objective is to create a backup system involving all the computers in the LAN. Communication between peers is acheived through udp multicast.
  
  To successfully run the program the following steps must be performed in the exact same order:
  
    1. Run the server.
    2. Run the TestApp with the desired arguments.
    3. If there is the need to send a new command repeat number 2.
    
  If the protocol to use is the enhanced version the TestApp must be called with the protocol name plus "ENH". For example, for the backup protocol the enhanced version must be called "BACKUPENH".
