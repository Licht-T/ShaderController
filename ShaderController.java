
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.media.opengl.GL2;


public class ShaderController {
	
	private enum SessionType {LINK,COMPILE};
	
	private int vertexShaderProgram;
	private int fragmentShaderProgram;
	private int shaderProgram;
	private String[] vertexSource;
	private String[] fragmentSource;
	private GL2 gl;
	
	public ShaderController(GL2 g){
		gl = g;
		vertexShaderProgram = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		fragmentShaderProgram = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		shaderProgram = gl.glCreateProgram();
	}

	public void shaderInit()
	{
		try{
			combineShaders();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void loadShaderFiles(String vertexFile, String fragmentFile){
		fragmentSource = loadFile(fragmentFile);
		vertexSource = loadFile(vertexFile);
	}

	private String[] loadFile( String filename ){
		StringBuilder sb = new StringBuilder();
		try
		{
			InputStream is = getClass().getResourceAsStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append('\n');
			}
			is.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return new String[]{sb.toString()};
	}
	

	
	private void printShaderInfo(SessionType session,int program,IntBuffer intBuffer,boolean err){
		
		PrintStream output = System.out;
		if(err){
			output = System.err;
		}
		
		if(session==SessionType.COMPILE){
			gl.glGetShaderiv(program, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			if(err)output.println("Shader Program compile error: ");
			else output.println("Shader Program compile output: ");
		}
		else if(session==SessionType.LINK){
			gl.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, intBuffer);
			if(err)output.println("Shader Program link error: ");
			else output.println("Shader Program link output: ");
		}
		int size = intBuffer.get(0);
		
		if (size > 0){
			ByteBuffer byteBuffer = ByteBuffer.allocate(size);
			
			if(session==SessionType.COMPILE){
				gl.glGetShaderInfoLog(program, size, intBuffer, byteBuffer);
			}
			else if(session==SessionType.LINK){
				gl.glGetProgramInfoLog(program, size, intBuffer, byteBuffer);
			}
			
			for (byte b : byteBuffer.array()){
				output.print((char) b);
			}
		}
		else{
			output.println("No output.");
		}
	}
	
	private void compileShader(int program, String[] code) throws Exception {
		boolean err;
		IntBuffer compiled = IntBuffer.allocate(1);
		
		gl.glShaderSource(program, 1, code, null, 0);
		gl.glCompileShader(program);
		gl.glGetShaderiv(program, GL2.GL_COMPILE_STATUS, compiled );
		err = (compiled.get(0)==GL2.GL_FALSE);
		printShaderInfo(SessionType.COMPILE,program,compiled,err);
		if (err){
			throw new Exception("Failed compilation.");
		}
	}

	private void combineShaders() throws Exception
	{
		boolean err;
		compileShader(vertexShaderProgram,vertexSource);
		compileShader(fragmentShaderProgram,fragmentSource);
		
		gl.glAttachShader(shaderProgram, vertexShaderProgram);
		gl.glAttachShader(shaderProgram, fragmentShaderProgram);
		gl.glLinkProgram(shaderProgram);
		gl.glValidateProgram(shaderProgram);
		
		IntBuffer linked = IntBuffer.allocate(1);
		gl.glGetProgramiv(shaderProgram, GL2.GL_LINK_STATUS, linked);

		
		err = (linked.get(0)==GL2.GL_FALSE);
		printShaderInfo(SessionType.LINK,shaderProgram,linked,err);
		if (err){
			throw new Exception("Failed Link.");
		}
	}

	public int enable(){
		gl.glUseProgram(shaderProgram);
		return shaderProgram;
	}

	public int disable(){
		gl.glUseProgram(0);
		return 0;
	}
	
	
	public int glGetUniformLocation(String s){
		return gl.glGetUniformLocation(shaderProgram, s);
	}
}