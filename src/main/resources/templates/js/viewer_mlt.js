<!DOCTYPE html>
<html>
<head>

<title>Viewer with texture</title>

<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <script src="js/three.js"></script>
    <script src="js/OBJLoader.js"></script>
    <script src="js/OrbitControls.js"></script>
    <script src="js/Detector.js"></script>
    <script src="js/MTLLoader.js"></script>


    </head>
    <body>

    <script>

if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
}

var container;

var camera, controls, scene, renderer;
var lighting, ambient, keyLight, fillLight, backLight;

init();
animate();

function init() {

    container = document.createElement('div');
    document.body.appendChild(container);

    //camera

    camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 1000);
    camera.position.z = 3;

    //scene

    scene = new THREE.Scene();
    lighting = false;

    ambient = new THREE.AmbientLight(0xffffff, 1.0);
    scene.add(ambient);

    keyLight = new THREE.DirectionalLight(new THREE.Color('hsl(30, 100%, 75%)'), 1.0);
    keyLight.position.set(-100, 0, 100);

    fillLight = new THREE.DirectionalLight(new THREE.Color('hsl(240, 100%, 75%)'), 0.75);
    fillLight.position.set(100, 0, 100);

    backLight = new THREE.DirectionalLight(0xffffff, 1.0);
    backLight.position.set(100, 0, -100).normalize();

    // model

    var mtlLoader = new THREE.MTLLoader();
    mtlLoader.setPath('ex/');
    mtlLoader.load('female-croupier-2013-03-26.mtl', function (materials) {

        materials.preload();

        var objLoader = new THREE.OBJLoader();
        objLoader.setMaterials(materials);
        objLoader.setPath('ex/');
        objLoader.load('female-croupier-2013-03-26.obj', function (object) {

            scene.add(object);

        });

    });

    //render

    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setClearColor(new THREE.Color("hsl(0, 0%, 10%)"));

    container.appendChild(renderer.domElement);

    // controls

    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.25;
    controls.enableZoom = true;

    //events

    window.addEventListener('resize', onWindowResize, false);

}

function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(window.innerWidth, window.innerHeight);

}

function animate() {

    requestAnimationFrame(animate);
    controls.update();
    render();
}

function render() {

    renderer.render(scene, camera);

}

</script>

</body>
</html>