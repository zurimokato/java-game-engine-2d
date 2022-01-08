package jade;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window {
    private  int width, height;
    private String title;
    private static Window window=null;
    private long glfwWindow;
    private Window(){
        this.height=1080;
        this.width=1920;
        title="Mario";
    }

    public static Window get() {
        if(window==null){
           Window.window=new Window();
        }
        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGl "+ Version.getVersion()+ "!" );
        
        init();
        loop();

        //free memory
        Callbacks.glfwFreeCallbacks(glfwWindow);
        GLFW.glfwDestroyWindow(glfwWindow);

        //terminate GLFW and free error callbacks

        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();



    }

    public void init(){
        //setup the error callback
        GLFWErrorCallback.createPrint(System.err).set();
        if(!GLFW.glfwInit()){
            throw  new IllegalStateException("unable to initialize GLFW ");
        }

        //configure
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE,GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED,GLFW.GLFW_TRUE);

        //create window
        glfwWindow=GLFW.glfwCreateWindow(this.width,this.height,this.title,MemoryUtil.NULL,MemoryUtil.NULL);
        if(glfwWindow==MemoryUtil.NULL){
            throw  new IllegalStateException("Failed to create the GLFW window.");
        }

        GLFW.glfwSetCursorPosCallback(glfwWindow,MouseListener::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(glfwWindow,MouseListener::mouseButtonCallback);
        GLFW.glfwSetScrollCallback(glfwWindow,MouseListener::mouseScrollCallback);

        // make the OpenGL Context current
        GLFW.glfwMakeContextCurrent(glfwWindow);
        //enable v-sync
        GLFW.glfwSwapInterval(1);

        GLFW.glfwShowWindow(glfwWindow);

        GL.createCapabilities();
    }


    public void loop(){
        while(!GLFW.glfwWindowShouldClose(glfwWindow)){
            //pool events
            GLFW.glfwPollEvents();

            GL11.glClearColor(1.0f,0.0f,0.0f,1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            GLFW.glfwSwapBuffers(glfwWindow);
        }
    }
}
