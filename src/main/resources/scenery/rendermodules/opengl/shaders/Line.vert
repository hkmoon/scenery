#version 410 core
 
layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec3 vertexNormal;
layout(location = 2) in vec2 vertexTexCoord;

out VertexData {
    vec3 Position;
    vec3 Normal;
    vec2 TexCoord;
    vec3 FragPosition;
    vec4 Color;
} VertexOut;

uniform mat4 ModelViewMatrix;
uniform mat4 ModelMatrix;
uniform mat3 NormalMatrix;
uniform mat4 ProjectionMatrix;
uniform mat4 MVP;
uniform vec3 CamPosition;

uniform int vertexCount;
uniform vec3 startColor;
uniform vec3 endColor;
uniform vec3 lineColor;
uniform int capLength;

void main()
{
   VertexOut.Normal = transpose(inverse(mat3(ModelMatrix)))*vertexNormal;
   VertexOut.Position = vec3(ModelViewMatrix*vec4(vertexPosition, 1.0));
   VertexOut.TexCoord = vertexTexCoord;
   VertexOut.FragPosition = vec3(ModelMatrix * vec4(vertexPosition, 1.0));

   gl_Position = MVP * vec4(vertexPosition, 1.0);

   VertexOut.Color = vec4(lineColor, 1.0);

   if(gl_VertexID < capLength) {
        VertexOut.Color = vec4(startColor, 1.0);
   }

   if(gl_VertexID > vertexCount-capLength) {
        VertexOut.Color = vec4(endColor, 1.0);
   }
}
