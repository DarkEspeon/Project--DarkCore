#version 330

uniform sampler2D colorBuff, normalBuff, depthBuff, posBuff;

uniform mat4 viewMat;

uniform vec3 att;
uniform vec3 lightPos;
uniform vec3 lightCol;

uniform vec3 cameraPos;

uniform float shineDamper;
uniform float reflectivity;

in vec3 texCoord;

layout (location = 0) out vec4 glCol;
vec4 calcLight(vec3 lightColor, vec3 lightPosi, vec3 LightDir, vec3 worldPos, vec3 Normal){
	vec4 AmbientColor = vec4(lightColor, 1.0) * 0.2;
	float DiffuseFactor = dot(Normal, -LightDir);
	
	vec4 DiffuseColor = vec4(0);
	vec4 SpecularColor = vec4(0);
	
	if(DiffuseFactor > 0.0){
		DiffuseColor = vec4(lightColor, 1.0) * DiffuseFactor;
		vec3 VertexToEye = normalize(cameraPos - worldPos);
		vec3 LightReflect = normalize(reflect(LightDir, Normal));
		float SpecularFactor = dot(VertexToEye, LightReflect);
		SpecularFactor = pow(SpecularFactor, shineDamper);
		if(SpecularFactor > 0.0){
			SpecularColor = vec4(lightColor, 1.0) * reflectivity * SpecularFactor;
		}
	}
	return (DiffuseColor + SpecularColor);
}
vec4 calcPointLight(vec3 worldPos, vec3 Normal){
	vec3 LightDir = worldPos - lightPos;
	float Distance = length(LightDir);
	LightDir = normalize(LightDir);
	
	vec4 Color = calcLight(lightCol, lightPos, LightDir, worldPos, Normal);
	
	float Attenuation = att.x;
	Attenuation += att.y * Distance;
	Attenuation += att.z * Distance * Distance;
	Attenuation = max(1.0, Attenuation);
	return Color / Attenuation;
}
void main(){
	glCol = vec4(1.0, 1.0, 1.0, 0.0);
	vec3 Normal = normalize(texture(normalBuff, texCoord.st).rgb * 2.0 - 1.0);
	vec4 Position = (texture(posBuff, texCoord.st) * 2.0 - 1.0);
	
	glCol = calcPointLight(Position.xyz, Normal);
}