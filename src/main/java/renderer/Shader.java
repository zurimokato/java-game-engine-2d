package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

    private int shaderProgramID;
    private String vertexSource, fragmentSource;
    private final String filePath;

    public Shader(String filePath){
        this.filePath=filePath;
        try{
            String source=new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString= source.split("(#type)( )+([a-zA-Z]+)");

            int index = source.indexOf("#type")+6;
            int eol=source.indexOf("\r\n", index);


            String firstParent=source.substring(index,eol).trim();


            index=source.indexOf("#type",eol)+6;
            eol=source.indexOf("\r\n",index);
            String secondParent=source.substring(index,eol).trim();


            if(firstParent.equals("vertex")){
                vertexSource=splitString[1];
            }else if(firstParent.equals("fragment")){
                fragmentSource=splitString[1];
            }else{
                throw new IOException("Unexpected token "+firstParent+ " in "+ filePath);
            }

            if(secondParent.equals("vertex")){
                vertexSource=splitString[2];
            }else if(secondParent.equals("fragment")){
                fragmentSource=splitString[2];
            }else{
                throw new IOException("Unexpected token "+firstParent+ " in "+ filePath);
            }
        }catch (IOException e){
            e.printStackTrace();
            assert false : "ERROR: Could  not open  file for shader: '"+filePath+"'";
        }
    }

    public  void compile(){

        //==========================================
        //          compile and link shaders
        //==========================================
        int vertexID,fragmentID;
        //First load and compile the fragment shader
        vertexID= glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexID,vertexSource);
        glCompileShader(vertexID);

        //check for errors

        int success=glGetShaderi(vertexID, GL_COMPILE_STATUS);

        if(success== GL_FALSE){
            int len= glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filePath+"'\n\tVertex shader compilation  failed");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false: "";
        }

        //Second load and compile the fragment shader
        fragmentID= glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID,fragmentSource);
        glCompileShader(fragmentID);

        //check for errors

        success=glGetShaderi(fragmentID,GL_COMPILE_STATUS);

        if(success== GL_FALSE){
            int len= glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filePath+"'\n\tFragment shader compilation  failed");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false: "";
        }

        //link shaders and check for error

        shaderProgramID=glCreateProgram();
        glAttachShader(shaderProgramID,vertexID);
        glAttachShader(shaderProgramID,fragmentID);
        glLinkProgram(shaderProgramID);

        //check for linking success
        success= glGetProgrami(shaderProgramID,GL_LINK_STATUS);

        if (success==GL_FALSE){
            int len= glGetProgrami(shaderProgramID,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '"+filePath+"'\n\tLinking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID,len));
            assert false: "";
        }

    }
    
    public void use(){
        glUseProgram(shaderProgramID);
    }

    public  void detach(){
        glUseProgram(0);
    }
}
