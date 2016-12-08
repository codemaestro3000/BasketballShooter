#version 150

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec4 set_color;

in vec3 in_position;
in vec3 in_normal;
in vec2 in_tex_coord;

out vec2 pass_tex_coord;
out vec3 pass_frag_pos;
out vec3 pass_normal;

void main() {
    vec4 world_pos = modelMatrix * vec4(in_position, 1.0f);
    pass_frag_pos = world_pos.xyz;
    gl_Position = projectionMatrix * viewMatrix * world_pos;
    pass_tex_coord = in_tex_coord;
    
    mat3 normal_matrix = transpose(inverse(mat3(modelMatrix)));
    pass_normal = normal_matrix * in_normal;
}