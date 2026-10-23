# Jester's Tricks — Forge 1.20.1

Armas y trucos del Jester, el bufón arlequín. Todo sale **solo de la pestaña "Jester's Tricks" del modo creativo**: no hay recetas ni botín.

## Objetos

| Objeto | Qué hace |
|---|---|
| Cetro del Bufón | 16 de daño + Marchitamiento II. Clic derecho: tormenta de pétalos negros (náusea, ceguera, lentitud y daño a 7 bloques). |
| Naipes Afilados | Abanico de 3 naipes (10 de daño cada uno). Agachado: un as de 24 de daño. 3D en la mano. |
| Cuchillos de Malabarista | Cuchillo de 18 de daño que vuelve a tu mano como un bumerán. 3D en la mano. |
| Mazo de Goma | Poco daño, retroceso enorme y "¡boing!". Clic derecho: martillazo al suelo. 3D en la mano, 2D en el inventario. |
| Pistola BANG! | Dispara banderitas de payaso. Explotan: 4 corazones que atraviesan armadura y **nunca matan**, y aplican **Pranked!** 10 s. 3D en la mano. |
| Chistera | Saca un conejo, un objeto al azar (a veces muy bueno) o murciélagos. |
| Varita | Intercambia tu posición con una criatura. Agachado: teletransporte a donde miras. |
| Bomba de Humo | Invisibilidad + Velocidad III, ciega a todo lo cercano y te pierden de vista. |
| Pañuelos Infinitos | Gancho: te lleva al bloque o atrae criaturas. Agáchate para soltarte. |
| Flor de Solapa | Chorro de agua: empuja, apaga fuego y destroza a Endermen y Blazes. |
| Caja Sorpresa (bloque) | Al pisarla o abrirla sale el Jester y te lanza por los aires. |
| Cojín de Pedorretas (bloque) | Quien lo pisa suelta una pedorreta y queda mareado. |

### Poderes de su mundo de origen

| Objeto | Qué hace |
|---|---|
| Estrella del Origen | Abre una grieta y te lleva a otra dimensión (posición equivalente, siempre en un sitio seguro). Agachado: eliges destino (mundo normal, Nether o End). |
| Aro de Feria | Agachado + clic en el suelo: aro **fijo** para siempre (si pones otro fijo, el anterior desaparece). Clic normal: aro temporal. Al saltar por un temporal, este desaparece y apareces en el fijo, aunque esté en otra dimensión. Los temporales sin usar se desvanecen a los 2 minutos. |
| Caja Negra | Clic: bolsillo de 27 huecos que viaja con la caja. Agachado sobre una criatura: se la traga (ni jugadores ni jefes). Agachado en el suelo o al aire: la suelta. |
| Baraja del Destino | Sobre otro jugador o criatura: carta mala al azar (Pranked!, levitar, brillar, lentitud, mareo, ceguera, ¡a bailar!, teletransporte...). Al aire: carta buena para ti (fuerza, velocidad, regeneración, resistencia...). |
| Espejo de la Risa | Salen tres copias del Jester con su skin. Los monstruos van a por ellas y estallan en confeti al golpearlas. |
| Cascabeles | Todo lo que hay a 10 bloques se pone a bailar, y los invisibles dejan de serlo y brillan. |

La Varita ahora abre mini portales en cada extremo del salto.

**Pranked!**: mientras dura, la armadura pierde sus encantamientos (Protección y sus variantes, Caída de pluma y Espinas).

Los proyectiles (naipe, cuchillo, banderita y nudo de pañuelos) vuelan con modelos 3D.

## Solo para el Jester

Todos los objetos funcionan únicamente para los jugadores que pongas en la configuración del servidor. Los demás reciben un aviso y una risita.

1. Arranca el mundo o el servidor una vez con el mod.
2. Abre `world/serverconfig/jestertricks-server.toml` (en un servidor, dentro de la carpeta del mundo).
3. Pon el nombre del Jester: `jesterNames = ["NombreDelJester"]`.
4. Reinicia.

Si la lista está vacía, cualquiera puede usarlos (así se puede probar el mod).

## Compilar el .jar

1. Instala **JDK 17** (por ejemplo, Temurin 17).
2. Abre una terminal en esta carpeta y ejecuta:
   - Windows: `gradlew.bat build`
   - Mac/Linux: `./gradlew build`
3. La primera vez tarda (descarga Minecraft y Forge). El mod queda en `build/libs/jestertricks-1.20.1-1.0.0.jar`.
4. Copia ese .jar a la carpeta `mods` de tu Minecraft con Forge 1.20.1 (47.x).

Para probarlo sin instalar: `gradlew runClient`.

## Retocar

- Texturas (pixel art 32x32): `src/main/resources/assets/jestertricks/textures/`.
- Modelos 3D: `src/main/resources/assets/jestertricks/models/item/*_3d.json` (se abren con Blockbench). Ahí puedes ajustar cómo se sostienen en la mano (sección `display`).
- Textos en español e inglés: `assets/jestertricks/lang/es_es.json` y `en_us.json`.
