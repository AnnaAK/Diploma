
if (!Detector.webgl) {
    Detector.addGetWebGLMessage();
}

//var mouseX = 0, mouseY = 0;
var windowHalfX = window.innerWidth / 2;
var windowHalfY = window.innerHeight / 2;

var container;

var camera, scene, renderer;

var file = getURLParameter('file');
var f_texture = getURLParameter('f_texture');


init(file, f_texture);
animate();


function init(file, f_texture) {

    container = document.createElement( 'div' );
    document.body.appendChild( container );

    camera = new THREE.PerspectiveCamera( 45, window.innerWidth / window.innerHeight, 1, 800 );
    camera.position.z = 10;

    // scene

    scene = new THREE.Scene();

    var ambientLight = new THREE.AmbientLight( 0xcccccc, 0.4 );
    scene.add( ambientLight );

    var pointLight = new THREE.PointLight( 0xffffff, 0.8 );
    camera.add( pointLight );
    scene.add( camera );

    // texture

    var manager = new THREE.LoadingManager();
    manager.onProgress = function ( item, loaded, total ) {

        console.log( item, loaded, total );

    };

    var textureLoader = new THREE.TextureLoader( manager );
    var texture = textureLoader.load( f_texture );
    console.log(texture);

    // model

    var onProgress = function ( xhr ) {
        if ( xhr.lengthComputable ) {
            var percentComplete = xhr.loaded / xhr.total * 100;
            console.log( Math.round(percentComplete, 2) + '% downloaded' );
        }
    };

    var onError = function ( xhr ) {
    };

    var loader = new THREE.OBJLoader( manager );
    loader.load( file, function ( object ) {

        object.traverse( function ( child ) {

            if ( child instanceof THREE.Mesh ) {

                child.material.map = texture;
                if (texture.name == ""){ child.material.color.setHex(0x99ffff);}

            }

        } );

        scene.add( object );

        //
        var pos = object.position;
        camera.position.set(pos.x, pos.y, 400);
        camera.lookAt(pos);
        var boxFrmScene = new THREE.Box3().setFromObject(scene);
        var height = Math.max(boxFrmScene.size().y, boxFrmScene.size().x);
        var fov = camera.fov * (Math.PI / 180);
        var distance = Math.abs(height / Math.sin(fov / 1.8));
        camera.position.set(pos.x , pos.y, distance + (height / 2));
        camera.updateProjectionMatrix();

    }, onProgress, onError );

    //

    /* Renderer */
    renderer = new THREE.WebGLRenderer();
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setClearColor(new THREE.Color("hsl(0, 0%, 98%)"));
    container.appendChild(renderer.domElement);
    //document.addEventListener( 'mousewheel', onDocumentMouseWheel, false );

    //

    window.addEventListener( 'resize', onWindowResize, false );

    /* Controls */
    controls = new THREE.OrbitControls(camera, renderer.domElement);
    controls.enableDamping = false;
    controls.dampingFactor = 0.4;
    controls.enableZoom = true;

}

function onWindowResize() {

    windowHalfX = window.innerWidth / 2;
    windowHalfY = window.innerHeight / 2;

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight );

}

function onDocumentMouseWheel( event ) {

    fov -= event.wheelDeltaY * 0.1;
    camera.projectionMatrix = THREE.Matrix4.makePerspective( fov, window.innerWidth / window.innerHeight, 1, 100 );
    camera.updateProjectionMatrix();

}
//

function animate() {

    requestAnimationFrame( animate );
    render();

}

function render() {

    controls.update();
    renderer.render( scene, camera );

}

function getURLParameter(name) {
    return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search) || [null, ''])[1].replace(/\+/g, '%20')) || "";
}

