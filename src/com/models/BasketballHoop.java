package com.models;

import java.util.ArrayList;

import com.graphics.Model;
import com.graphics.RenderableMesh;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.util.math.Mat4;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.charon.global.world.SphericalObject;
import com.viduus.charon.global.world.TriangularObject;
import com.viduus.util.models.ModelData;
import com.viduus.util.models.geometries.Polylist;

public class BasketballHoop extends Model {
	
	public SphericalObject rim;

	public BasketballHoop(ModelData model_data) {
		super(model_data);
		
		Mat4 model_matrix = new Mat4();
		model_matrix.translate(120f, -0.6f, -7.0f);                                      
		model_matrix.scale(12.0f, 12.0f, 12.0f);                                         
		model_matrix.rotate((float) Math.toRadians(-90), (float) Math.toRadians(-90), 0);
		for(TriangularObject t : this.faces) {
			t.v1 = Mat4.multiply(model_matrix, t.v1);
			t.v2 = Mat4.multiply(model_matrix, t.v2);
			t.v3 = Mat4.multiply(model_matrix, t.v3);
		}
		
		ArrayList<Vec3> rim_vertices = new ArrayList<Vec3>();
		
		for(RenderableMesh rm : this.meshes) {
			if(rm.getName().equals("11:1")) {
				for(Polylist pList : rm.mesh.polylists) {
					float[] gpu_buffer = pList.getGPUBuffer(0, null);
		
					int num_vertices = gpu_buffer.length / pList.elements_per_vertex;
					for(int i = 0; i < num_vertices; i += 3) {
						Vec3 v1, v2, v3;
						
						v1 = new Vec3(gpu_buffer[(i + 0) * pList.elements_per_vertex + 0], gpu_buffer[(i + 0) * pList.elements_per_vertex + 1], gpu_buffer[(i + 0) * pList.elements_per_vertex + 2]);
						v2 = new Vec3(gpu_buffer[(i + 1) * pList.elements_per_vertex + 0], gpu_buffer[(i + 1) * pList.elements_per_vertex + 1], gpu_buffer[(i + 1) * pList.elements_per_vertex + 2]);
						v3 = new Vec3(gpu_buffer[(i + 2) * pList.elements_per_vertex + 0], gpu_buffer[(i + 2) * pList.elements_per_vertex + 1], gpu_buffer[(i + 2) * pList.elements_per_vertex + 2]);
							
						rim_vertices.add(Mat4.multiply(model_matrix, v1));
						rim_vertices.add(Mat4.multiply(model_matrix, v2));
						rim_vertices.add(Mat4.multiply(model_matrix, v3));
					}
				}
				break;
			}
		}
		
		float min_y = Float.MAX_VALUE, min_z = Float.MAX_VALUE, max_z = Float.MIN_VALUE, min_x = Float.MAX_VALUE, max_x = Float.MIN_VALUE;
		for(Vec3 v : rim_vertices) {
			if(v.y < min_y)
				min_y = v.y;
			if(v.z < min_z)
				min_z = v.z;
			else if(v.z > max_z)
				max_z = v.z;
			
			if(v.x < min_x)
				min_x = v.x;
			else if(v.x > max_x)
				max_x = v.x;
		}
		
		rim = new SphericalObject(new Vec3((max_x - min_x) / 2.0f + min_x, min_y, (max_z - min_z) / 2.0f + min_z), (max_z - min_z) / 2.0f);
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		
		graphics.model_matrix.translate(120f, -0.6f, -8.3f);
		graphics.model_matrix.scale(12.0f, 12.0f, 12.0f);
		graphics.model_matrix.rotate((float) Math.toRadians(-90), (float) Math.toRadians(-90), 0);
		super.render(graphics);
	}
}
