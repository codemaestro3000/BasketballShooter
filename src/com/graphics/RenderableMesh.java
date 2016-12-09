package com.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLRenderable;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderVariable;
import com.viduus.util.models.geometries.Mesh;
import com.viduus.util.models.geometries.Polylist;

public class RenderableMesh implements OpenGLRenderable{
	
	private boolean has_init_vbo_vao = false;
	private IntBuffer vbo;
	private IntBuffer vao;
	
	private final Mesh mesh;

	public RenderableMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public String getName() {
		return this.mesh.getName();
	}

	@Override
	public void render(final OpenGLGraphics graphics) {
		
		for(Polylist pList : this.mesh.polylists) {
			try {
				
				/*-----------------------------------------------------------------
				 * Render the scene to the gbuffer
				 */
				
				if( !has_init_vbo_vao ){
					// Allocate memory for the vbo and vao memory locations
					vao = BufferUtils.createIntBuffer(1);
					vbo = BufferUtils.createIntBuffer(2);
	
					GL30.glGenVertexArrays( vao );
					GL15.glGenBuffers( vbo );
					
					has_init_vbo_vao = true;
				}
				
				// Bind the current Vertex Array Object
		        GL30.glBindVertexArray( vao.get(0) );
				
				// Bind the current Vertex Buffer Object
				GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, vbo.get(1) );
				
				// Load the indices data into a buffer
				ShortBuffer ibo_data = graphics.memory_manager.prepareIndexData( pList.getIBOBuffer() );
				
				// Send the indices data to the GPU
				GL15.glBufferData( GL15.GL_ELEMENT_ARRAY_BUFFER, ibo_data, GL15.GL_STATIC_DRAW );
				
				// Clear the bind
				GL15.glBindBuffer( GL15.GL_ELEMENT_ARRAY_BUFFER, 0 );
		
				// Bind the vertex data memeory location
				GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, vbo.get(0) );
		
				// Load the vertex data into a buffer
				FloatBuffer vbo_data = graphics.memory_manager.prepareVertexData(pList.getGPUBuffer(0, null));
				
				// Send the vertex data to the GPU
				GL15.glBufferData( GL15.GL_ARRAY_BUFFER, vbo_data, GL15.GL_STATIC_DRAW );
				
				ShaderProgram active_shader = graphics.shader_manager.getActiveShader();
				
				// Get shader variables
				ShaderVariable in_Position = active_shader.getAttributeVariable("in_position");
				ShaderVariable in_Normal = active_shader.getAttributeVariable("in_normal");
				ShaderVariable in_TexCord = active_shader.getAttributeVariable("in_tex_coord");
				
				// Set attribute values, offsets, and strides
				GL20.glEnableVertexAttribArray( in_Position.getId() );    //We like submitting vertices on stream 0 for no special reason
				GL20.glVertexAttribPointer( in_Position.getId(), 3, GL11.GL_FLOAT, false, pList.elements_per_vertex * Float.BYTES, 0 );
				GL20.glEnableVertexAttribArray( in_Normal.getId() );    //We like submitting texcoords on stream 2 for no special reason
				GL20.glVertexAttribPointer( in_Normal.getId(), 3, GL11.GL_FLOAT, false, pList.elements_per_vertex * Float.BYTES, 3*Float.BYTES );
				GL20.glEnableVertexAttribArray( in_TexCord.getId() );    //We like submitting texcoords on stream 2 for no special reason
				GL20.glVertexAttribPointer( in_TexCord.getId(), 2, GL11.GL_FLOAT, false, pList.elements_per_vertex * Float.BYTES, 6*Float.BYTES );
				
				// Clear buffers
				GL15.glBindBuffer( GL15.GL_ARRAY_BUFFER, 0 );
		        GL30.glBindVertexArray( 0 );
	
		        // Deallocate data buffers
				ibo_data.clear();
				vbo_data.clear();
				
				/*-----------------------------------------------------------------
				 * Draw what we just bound to the GPU
				 */
		
				// Bind the font texture
	//			GL11.glBindTexture( GL11.GL_TEXTURE_2D, tid.get(0) );
		        
				// Bind to the VAO that has all the information about the vertices
		        GL30.glBindVertexArray( vao.get(0) );
		        
		        // Enable the attribute variables
		        GL20.glEnableVertexAttribArray( in_Position.getId() );
		        GL20.glEnableVertexAttribArray( in_Normal.getId() );
		        GL20.glEnableVertexAttribArray( in_TexCord.getId() );
				
		        // Bind to the index VBO that has all the information about the order of the vertices
				GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo.get(1));
				
				// Draw everything
				GL11.glDrawElements(GL11.GL_TRIANGLES, pList.getIBOBuffer().length, GL11.GL_UNSIGNED_SHORT, 0);
				
			    // Put everything back to default
		        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		        GL20.glDisableVertexAttribArray( in_Position.getId() );
		        GL20.glDisableVertexAttribArray( in_Normal.getId() );
		        GL20.glDisableVertexAttribArray( in_TexCord.getId() );
		        GL30.glBindVertexArray(0);
		        
			} catch (ShaderException e) {
				ErrorHandler.catchError(e);
			}
		}
	}
}
