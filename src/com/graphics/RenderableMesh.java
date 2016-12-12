package com.graphics;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

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
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.charon.global.world.TriangularObject;
import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.effects.Effect;
import com.viduus.util.models.effects.LightingModel;
import com.viduus.util.models.effects.PhongModel;
import com.viduus.util.models.geometries.Mesh;
import com.viduus.util.models.geometries.Polylist;
import com.viduus.util.models.materials.Material;
import com.viduus.util.models.util.Color;

public class RenderableMesh implements OpenGLRenderable{
	
	private boolean has_init_vbo_vao = false;
	private IntBuffer vbo;
	private IntBuffer vao;
	
	public final Mesh mesh;

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
				FloatBuffer vbo_data = graphics.memory_manager.prepareVertexData(pList.getGPUBuffer());
				
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
				 * Bind material information to GPU
				 */
				Material poly_material = mesh.materials.get(pList.material_symbol);
				Effect poly_effect = poly_material.effect;
				LightingModel lighting_model = poly_effect.lighting_model;
				try{
					if( lighting_model instanceof PhongModel ){
						PhongModel phong_model = (PhongModel) lighting_model;
						
						active_shader.getUniformVariable("ambient").setValue(new float[] {
							phong_model.ambient.r,
							phong_model.ambient.g,
							phong_model.ambient.b,
							phong_model.ambient.a
						});
	
						active_shader.getUniformVariable("emission").setValue(new float[] {
							phong_model.emission.r,
							phong_model.emission.g,
							phong_model.emission.b,
							phong_model.emission.a
						});
	
						active_shader.getUniformVariable("diffuse").setValue(new float[] {
							phong_model.diffuse.r,
							phong_model.diffuse.g,
							phong_model.diffuse.b,
							phong_model.diffuse.a
						});
	
						active_shader.getUniformVariable("specular").setValue(new float[] {
							phong_model.specular.r,
							phong_model.specular.g,
							phong_model.specular.b,
							phong_model.specular.a
						});
	
						active_shader.getUniformVariable("light_dir").setValue(new float[] {
							-0.5f,
							1,
							0
						});
						
						active_shader.getUniformVariable("shininess").setValue(phong_model.shininess);
						
						OutputHandler.println(pList.material_symbol);
						if( pList.material_symbol.equals("_2___Default-material") ){
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								1,
								1,
								1,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__40-material") ){ // yellow hat 
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0.55f,
								0.35f,
								0,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__37-material") ){ // yellow hat 
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								1,
								0.63f,
								0,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__36-material") ){ // yellow hat 
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0.984f,
								0.878f,
								0.333f,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__24-material") ){ // nose
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0,
								0,
								0,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__23-material") ){ // skin
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0.921f,
								0.804f,
								0.765f,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__22-material") ){ // blue pants
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0,
								0.42f,
								0.75f,
								1
							});
						}
						else if( pList.material_symbol.equals("Material__22-material") ){ // blue pants
							active_shader.getUniformVariable("diffuse").setValue(new float[] {
								0,
								0.42f,
								0.75f,
								1
							});
						}
						else if( pList.material_symbol.equals("material288-material") ){ // desert
							active_shader.getUniformVariable("using_texture").setValue(1);
							graphics.texture_manager.bind(
								graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image097D1400_097D13B0.png"),
								active_shader.getUniformVariable("texture_diffuse")
							);
						}
						else if( pList.material_symbol.equals("material822-material") ){ // blue pants
							active_shader.getUniformVariable("using_texture").setValue(1);
							graphics.texture_manager.bind(
								graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image097CD200_097CD1B0.png"),
								active_shader.getUniformVariable("texture_diffuse")
							);
						}
						else if( pList.material_symbol.equals("material1919-material") ){ // blue pants
							active_shader.getUniformVariable("using_texture").setValue(1);
							graphics.texture_manager.bind(
								graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image097CB100_097CB0B0.png"),
								active_shader.getUniformVariable("texture_diffuse")
							);
						}
						else if( pList.material_symbol.equals("material36-material") ){ // blue pants
							active_shader.getUniformVariable("using_texture").setValue(1);
							graphics.texture_manager.bind(
								graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image097C9000_097C8FB0.png"),
								active_shader.getUniformVariable("texture_diffuse")
							);
						}
						else if( pList.material_symbol.equals("material1998-material") ){ // blue pants
							active_shader.getUniformVariable("using_texture").setValue(1);
							graphics.texture_manager.bind(
								graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image09760D80_09760D30.png"),
								active_shader.getUniformVariable("texture_diffuse")
							);
						}
					}
				}catch( ShaderException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
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
		        
				active_shader.getUniformVariable("using_texture").setValue(0);
		        
			} catch (ShaderException e) {
				ErrorHandler.catchError(e);
			}
		}
	}
}
