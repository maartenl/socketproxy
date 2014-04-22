socketproxy
===========

A simple Java multi-threaded Socket Proxy Server. It listens to incoming connections and fowards any communication to a server, whilst recording the entire conversation.

SocketProxyServer
-----------------

This is the main class of the library. It can be instantiated by a third party, and provided with the local port and the remote host/port of the server to redirect requests to.

SocketListener
--------------

A listener (currently a single one) can be added to the SocketProxyServer to listen to the communication. Communication is only provided at the end of the session.

SocketListener is the interface that needs to be implemented by the program that makes use of this library. It consequently needs to be registered as a Listener to an instance of the SocketProxyServer.
