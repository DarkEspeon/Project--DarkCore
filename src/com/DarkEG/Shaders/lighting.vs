#version 330

in vec3 pos;

out vec3 texCoord;

void main(){
	texCoord = pos;
	gl_Position = vec4(pos, 1.0) * 2.0 - 1.0;
}