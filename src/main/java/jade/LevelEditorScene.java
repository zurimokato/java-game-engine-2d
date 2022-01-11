package jade;

import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class LevelEditorScene extends Scene{
    private final String  vertexShaderSrc="#version 330 core\n" +
            "        layout (location=0) in vec3 aPos;\n" +
            "        layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "        out vec4 fColor;\n" +
            "\n" +
            "        void main(){\n" +
            "            fColor=aColor;\n" +
            "            gl_Position=vec4(aPos,1.0);\n" +
            "}";
    private final String fragmentShaderSrc="#version 330 core\n" +
            "\n" +
            "        in vec4 fColor;\n" +
            "        out vec4 color;\n" +
            "\n" +
            "        void main(){\n" +
            "            color=fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private final float[] vertexArray={
                    //position              //color
             0.5f, -0.5f,  0.0f,   1.0f,  0.0f,  0.0f,  1.0f, //bottom right
            -0.5f,  0.5f,  0.0f,   0.0f,  1.0f,  0.0f,  1.0f, //top left
             0.5f,  0.5f,  0.0f,   0.0f,  0.0f,  1.0f,  1.0f, //top right
            -0.5f, -0.5f,  0.0f,   1.0f,  1.0f,  0.0f,  1.0f, //bottom left
    };

    //IMPORTANT: Must be in counter-clockwise order
    private final int[] elementArray={
            2,1,0, //top right triangle
            0,1,3 // bottom left triangle
    };
    private  int vaoID, vboID, eboID;
    public LevelEditorScene(){

    }


    @Override
    public void init(){
        //==========================================
        //          compile and link shaders
        //==========================================

        //First load and compile the fragment shader
        vertexID= glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexID,vertexShaderSrc);
        glCompileShader(vertexID);

        //check for errors

        int success=glGetShaderi(vertexID, GL_COMPILE_STATUS);

        if(success== GL_FALSE){
            int len= glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation  failed");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false: "";
        }

        //Second load and compile the fragment shader
        fragmentID= glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID,fragmentShaderSrc);
        glCompileShader(fragmentID);

        //check for errors

        success=glGetShaderi(fragmentID,GL_COMPILE_STATUS);

        if(success== GL_FALSE){
            int len= glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation  failed");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false: "";
        }

        //link shaders and check for error

        shaderProgram=glCreateProgram();
        glAttachShader(shaderProgram,vertexID);
        glAttachShader(shaderProgram,fragmentID);
        glLinkProgram(shaderProgram);

        //check for linking success
        success= glGetProgrami(shaderProgram,GL_LINK_STATUS);

        if (success==GL_FALSE){
            int len= glGetProgrami(shaderProgram,GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tLinking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgram,len));
            assert false: "";
        }
        //=============================================================
        // Generate VAO, VPO, and EBO buffer objects, and send to GPU
        //=============================================================

        vaoID=glGenVertexArrays();
        glBindVertexArray(vaoID);

        // create float buffer of vertices
        FloatBuffer vertexBuffer  = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);

        //crate the indices  and upload
        IntBuffer elementBuffer=BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID=glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,elementBuffer,GL_STATIC_DRAW);

        // add the vertex attribute  pointers

        int positionsSize=3;
        int colorsSize=4;
        int floatSizeBytes=4;
        int vertexSizeByBites= (colorsSize+positionsSize)*floatSizeBytes;
        glVertexAttribPointer(0,positionsSize,GL_FLOAT,false, vertexSizeByBites,0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1,colorsSize,GL_FLOAT,false,vertexSizeByBites,positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);


    }

    @Override
    public void update(float dt) {
        //bind shader program
        glUseProgram(shaderProgram);

        //bind the vao tha were using

        glBindVertexArray(vaoID);

        //enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,elementArray.length,GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);


    }
}
