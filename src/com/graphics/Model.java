package com.graphics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLRenderable;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.charon.global.world.TriangularObject;
import com.viduus.util.models.ModelData;
import com.viduus.util.models.geometries.Polylist;

public class Model implements OpenGLRenderable{

	private final ModelData model_data;
	protected ArrayList<RenderableMesh> meshes;
	public ArrayList<TriangularObject> faces;
	private static HashSet<String> meshes_to_ignore;
	
	/**
	 * Creates a new Model.
	 * @param model_name - The identifier for this model
	 * @param version - Collada version
	 * @param object_meshes - The mesh of vertices, normals, and texture points
	 */
	public Model(ModelData model_data) {
		//jankiest shit ever
		meshes_to_ignore = new HashSet<String>();
		meshes_to_ignore.add("40:1");
		meshes_to_ignore.add("50:1");
		meshes_to_ignore.add("60:1");
		meshes_to_ignore.add("70:1");
		meshes_to_ignore.add("41:1");
		meshes_to_ignore.add("51:1");
		meshes_to_ignore.add("61:1");
		meshes_to_ignore.add("71:1");
		meshes_to_ignore.add("32:1");
		meshes_to_ignore.add("42:1");
		meshes_to_ignore.add("52:1");
		meshes_to_ignore.add("62:1");
		meshes_to_ignore.add("72:1");
		meshes_to_ignore.add("33:1");
		meshes_to_ignore.add("43:1");
		meshes_to_ignore.add("53:1");
		meshes_to_ignore.add("63:1");
		meshes_to_ignore.add("73:1");
		meshes_to_ignore.add("24:1");
		meshes_to_ignore.add("34:1");
		meshes_to_ignore.add("44:1");
		meshes_to_ignore.add("54:1");
		meshes_to_ignore.add("64:1");
		meshes_to_ignore.add("25:1");
		meshes_to_ignore.add("35:1");
		meshes_to_ignore.add("45:1");
		meshes_to_ignore.add("55:1");
		meshes_to_ignore.add("65:1");
		meshes_to_ignore.add("26:1");
		meshes_to_ignore.add("36:1");
		meshes_to_ignore.add("46:1");
		meshes_to_ignore.add("56:1");
		meshes_to_ignore.add("66:1");
		meshes_to_ignore.add("27:1");
		meshes_to_ignore.add("37:1");
		meshes_to_ignore.add("47:1");
		meshes_to_ignore.add("57:1");
		meshes_to_ignore.add("67:1");
		meshes_to_ignore.add("28:1");
		meshes_to_ignore.add("38:1");
		meshes_to_ignore.add("48:1");
		meshes_to_ignore.add("58:1");
		meshes_to_ignore.add("68:1");
		meshes_to_ignore.add("29:1");
		meshes_to_ignore.add("39:1");
		meshes_to_ignore.add("49:1");
		meshes_to_ignore.add("59:1");
		meshes_to_ignore.add("69:1");
		
		this.model_data = model_data;
		this.meshes = new ArrayList<RenderableMesh>();
		
		for( com.viduus.util.models.geometries.Mesh m : model_data.mesh.values() ){
			meshes.add(new RenderableMesh(m));
		}
		
		faces = new ArrayList<TriangularObject>();
		
		for(RenderableMesh rm : this.meshes) {
			if(meshes_to_ignore.contains(rm.getName())) {
				continue;
			}
			
			for(Polylist pList : rm.mesh.polylists) {
				float[] gpu_buffer = pList.getGPUBuffer();
	
				int num_vertices = gpu_buffer.length / pList.elements_per_vertex;
				for(int i = 0; i < num_vertices; i += 3) {
					Vec3 v1, v2, v3, n1, n2, n3;
					
					v1 = new Vec3(gpu_buffer[(i + 0) * pList.elements_per_vertex + 0], gpu_buffer[(i + 0) * pList.elements_per_vertex + 1], gpu_buffer[(i + 0) * pList.elements_per_vertex + 2]);
					v2 = new Vec3(gpu_buffer[(i + 1) * pList.elements_per_vertex + 0], gpu_buffer[(i + 1) * pList.elements_per_vertex + 1], gpu_buffer[(i + 1) * pList.elements_per_vertex + 2]);
					v3 = new Vec3(gpu_buffer[(i + 2) * pList.elements_per_vertex + 0], gpu_buffer[(i + 2) * pList.elements_per_vertex + 1], gpu_buffer[(i + 2) * pList.elements_per_vertex + 2]);
					n1 = new Vec3(gpu_buffer[(i + 0) * pList.elements_per_vertex + 3], gpu_buffer[(i + 0) * pList.elements_per_vertex + 4], gpu_buffer[(i + 0) * pList.elements_per_vertex + 5]);
					n2 = new Vec3(gpu_buffer[(i + 1) * pList.elements_per_vertex + 3], gpu_buffer[(i + 1) * pList.elements_per_vertex + 4], gpu_buffer[(i + 1) * pList.elements_per_vertex + 5]);
					n3 = new Vec3(gpu_buffer[(i + 2) * pList.elements_per_vertex + 3], gpu_buffer[(i + 2) * pList.elements_per_vertex + 4], gpu_buffer[(i + 2) * pList.elements_per_vertex + 5]);
				
					Vec3 normal = new Vec3((n1.x + n2.x + n3.x) / 3.0f, (n1.y + n2.y + n3.y) / 3.0f, (n1.z + n2.z + n3.z) / 3.0f);
					faces.add(new TriangularObject(v1, v2, v3, normal));
				}
			}
		}
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		graphics.pushMatricies();
		
		try {
			ShaderProgram prog = graphics.shader_manager.getActiveShader();
			
			for( RenderableMesh m : this.meshes){
//				if( m.mesh.name.equals("Top_Ambi") ){
//					prog.getUniformVariable("using_texture").setValue(1);
//					graphics.texture_manager.bind(
//						graphics.texture_manager.loadImage(graphics, "./img/Basketball/Image09760D80_09760D30.png"),
//						prog.getUniformVariable("texture_diffuse")
//					);
//				}
				m.render( graphics );
			}
			
		} catch (ShaderException e) {
			ErrorHandler.catchError(e);
		}
		
		graphics.model_matrix.reset();
	}
}
