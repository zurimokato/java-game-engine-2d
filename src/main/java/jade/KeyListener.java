package jade;

import org.lwjgl.glfw.GLFW;

public class KeyListener {

    private  static  KeyListener instance;
    private static boolean keyPressed[]= new boolean[350];


    private KeyListener(){

    }

    public static KeyListener get(){
        if(KeyListener.instance==null){
            KeyListener.instance= new KeyListener();
        }

        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scanCode, int action, int mods){
        if(action== GLFW.GLFW_PRESS){
            get().keyPressed[key]=true;
        }else if(action==GLFW.GLFW_RELEASE){
            get().keyPressed[key]=false;
        }
    }


    public static  boolean isKeyPressed(int keyCode){
        return get().keyPressed[keyCode];

    }
}
