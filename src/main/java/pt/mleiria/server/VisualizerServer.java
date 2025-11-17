package pt.mleiria.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import pt.mleiria.core.JacksonUtils;
import pt.mleiria.rl.mdp.vo.AgentStatus;
import pt.mleiria.rl.mdp.vo.TaxiDriverAgentStatus;

import java.io.IOException;
import java.net.InetSocketAddress;

public class VisualizerServer extends WebSocketServer {

    public VisualizerServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Visualizer connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Visualizer disconnected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // We don't need to receive messages for this simple case
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Visualizer server started on port " + getPort());
    }

    /**
     * Sends the current state of the environment to all connected clients.
     *
     * @param agentPosition The 1D index of the agent's current position.
     */
    public void sendState(int agentPosition, final int epoch, final int stepCount, final double totalReward) {
        // We'll send a simple JSON-like string, e.g., {"agentPosition": 5}
        //String message = String.format("{\"agentPosition\": %d}", agentPosition);
        try {
            broadcast(JacksonUtils.encode(new AgentStatus(agentPosition, epoch, stepCount, totalReward)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> void sendState(T augmentedAgentStatus) {
        // We'll send a simple JSON-like string, e.g., {"agentPosition": 5}
        //String message = String.format("{\"agentPosition\": %d}", agentPosition);
        try {
            broadcast(JacksonUtils.encode(augmentedAgentStatus));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
