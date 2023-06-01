package fr.first92.bungeeapi.servers;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;

public class ServerManager {

    public ServerInfo createServerInfo(String name, Integer port) {

        InetSocketAddress socketAddress = new InetSocketAddress("localhost", port);

        return ProxyServer.getInstance().constructServerInfo(name,
                socketAddress, "Peu-Importe", false);
    }
}
