// externs/screeps.js

var ConstructionSite = function () { };

ConstructionSite.prototype = {
	id: "",
	owner: null,
	room: null,
	pos: null,
	progress: 0,
	progressTotal: 0,
	structureType: null,
	ticksToLive: 0,
	my: true,
	remove: function () { }
};
var Creep = function () { };

Creep.prototype = {
	id: null,
	name: null,
	owner: null,
	room: null,
	pos: null,
	memory: null,
	my: true,
	spawning: true,
	body: [ ],
	carry: { energy: 0 },
	carryCapacity: 0,
	hits: 0,
	hitsMax: 0,
	ticksToLive: 0,
	fatigue: 0,
	attack: function (target) { },
	build: function (target) { },
	cancelOrder: function (methodName) { },
	claimController: function (target) { },
	drop: function (resourceType, amount) { },
	getActiveBodyparts: function (type) { },
	harvest: function (target) { },
	heal: function (target) { },
	move: function (direction) { },
	moveByPath: function(path) { },
	moveTo: function (target, opts) { },
	notifyWhenAttacked: function(enabled){ },
	pickup: function (target) { },
	rangedAttack: function (target) { },
	rangedHeal: function(target) { },
	rangedMassAttack: function() { },
	repair: function (target) { },
	say: function(message) { },
	suicide: function () { },
	transfer: function (target, resourceType, amount) { },
	unclaimController: function(target) { },
	upgradeController: function(target) { }
};
var Energy = function () { };

Energy.prototype = {
	id: "",
	room: null,
	pos: null,
	energy: 0
};
var Exit = function () { };

Exit.prototype = {
	id: "",
	room: null,
	pos: null,
	exit: 0
};
var Flag = function () { };

Flag.prototype = {
	id: "",
	color: '',
	name: "",
	roomName: "",
	room: null,
	pos: null,
	memory: null,
	remove: function () { },
	setColor: function(color) { },
	setPosition: function(x, y) { }
};
var Game = function () { };

Game.prototype = {
	cpu: null,
	creeps: null,
	map: null,
	flags: null,
	rooms: null,
	spawns: null,
	structures: null,
	time: 0,
	getObjectById: function (id) { },
	getRoom: function (name) { },
	notify: function (message, groupInterval) { }
};

var Cpu = function () { };
Cpu.prototype = {
    limit: 0,
    tickLimit: 0,
    bucket: 0,
    getUsed: function () {}
};

var global = function () { };

global.prototype = {

    FIND_EXIT_TOP: 1,
    FIND_EXIT_BOTTOM: 5,
    FIND_EXIT: 10,
    FIND_MY_CREEPS: 102,
    FIND_SOURCES_ACTIVE: 104,
    FIND_DROPPED_ENERGY: 106,
    FIND_MY_STRUCTURES: 108,
    FIND_FLAGS: 110,
    FIND_MY_SPAWNS: 112,

    TOP: 1,
    RIGHT: 3,
    BOTTOM: 5,
    LEFT: 7,

    ERR_NOT_OWNER: -1,
    ERR_NAME_EXISTS: -3,
    ERR_NOT_FOUND: -5,
    ERR_INVALID_TARGET: -7,
    ERR_NOT_IN_RANGE: -9,
    ERR_TIRED: -11,
    ERR_NOT_ENOUGH_EXTENSIONS: -13,
    ERR_GCL_NOT_ENOUGH: -15,

    COLOR_RED: "red",
    COLOR_BLUE: "blue",
    COLOR_GREEN: "green",
    COLOR_ORANGE: "orange",
    COLOR_GREY: "grey",

    WORK: "work",
    ATTACK: "attack",
    TOUGH: "tough",
    BODYPARTS_ALL: ["move", "work", "carry", "attack", "ranged_attack", "tough", "heal"],

    STRUCTURE_SPAWN: "spawn",
    STRUCTURE_ROAD: "road",
    STRUCTURE_RAMPART: "rampart",
    STRUCTURE_PORTAL: "portal",
    STRUCTURE_LINK: "link",

    MODE_SURVIVAL: "survival",
    MODE_ARENA: "arena",
    MODE_SIMULATION: "simulation",
    MODE_WORLD: "world",

    Game: null,

    module: null,

    exports: null,

    global: this

};
var Map = function () { };

Map.prototype = {
    findExit: function(fromRoom, toRoom) { },
    findRoute: function(fromRoom, toRoom) { },
    describeExits: function(roomName) { },
    isRoomProtected: function(roomName) { }
};
var Memory = {
    rooms: [],
    spawns: []
};
var Owner = function() { };

Owner.prototype = {
	username: ""
};
var PathStep = function() { };

PathStep.prototype = {
	x: 0,
	y: 0,
	dx: 0,
	dy: 0,
	direction: null
};
var Room = function() { };

Room.prototype = {
	controller: null,
	storage: null,
	memory: null,
	mode: '',
	name: "",
	energyAvailable: 0,
	energyCapacityAvailable: 0,
	survivalInfo: null,
	createFlag: function(pos, name, color) { },
	createConstructionSite: function(pos, structureType) { },
	find: function(type, opts) { },
	findExitTo: function(room) { },
	lookAt: function(target, opts) { },
	lookAtArea: function(top, left, bottom, right) { },
	lookForAt: function(type, target) { },
	lookForAtArea: function(type, top, left, bottom, right) { },
	findPath: function(fromPos, toPos, opts) { },
	getPositionAt: function(x, y) { },
	makeSnapshot: function(description) { }
};
var RoomPosition = function (x, y, roomName) { };

RoomPosition.prototype = {
	x: 0,
	y: 0,
	roomName: "",
	inRangeTo: function (toPos, range) { },
	isNearTo: function (target) { },
	getDirectionTo: function (target) { },
	findPathTo: function (target, opts) { },
	findNearest: function (type, opts) { },
	findInRange: function (type, range, opts) { },
	findClosestByPath: function(type, opts) { },
	findClosestByRange: function(type, opts) { },
	getRangeTo: function(target) { },
	isEqualTo: function (target) { },
  isEqualTo: function (x, y) { },
	look: function() { },
	lookFor: function(type) { },
	createFlag: function(name, color) { },
	createConstructionSite: function(structureType) { }
};
var Source = function () { };

Source.prototype = {
	id: "",
	room: null,
	pos: null,
	energy: 0,
	energyCapacity: 0,
	ticksToRegeneration: 0
};
var Spawn = function () { };

Spawn.prototype = {
	id: "",
	name: "",
	owner: null,
	room: null,
	pos: null,
	memory: [ ],
	my: true,
	structureType: 'spawn',
	spawning: null,
	energy: 0,
	energyCapacity: 0,
	hits: 0,
	hitsMax: 0,
	canCreateCreep: function(body, name) { },
	createCreep: function (bodyParts, name, memory) { },
	destroy: function() { },
	notifyWhenAttacked: function(enabled) { },
	transferEnergy: function (target, amount) { },
  renewCreep: function (target) { }
};
var Structure_Extension = function() { };
Structure_Extension.prototype = {
    structureType: global.STRUCTURE_EXTENSION,
    energy: 0,
    energyCapacity: 0,
    transferEnergy: function(target, amount) { }
};

var Structure_Link = function() { };
Structure_Link.prototype = {
    structureType: global.STRUCTURE_LINK,
    cooldown: 0,
    energy: 0,
    energyCapacity: 0,
    transferEnergy: function(target, amount) { }
};

var Structure_Keeper_Lair = function() { };
Structure_Keeper_Lair.prototype = {
    structureType: global.STRUCTURE_KEEPER_LAIR,
    ticksToSpawn: 0
};

var Structure_Controller = function() { };
Structure_Controller.prototype = {
    structureType: global.STRUCTURE_CONTROLLER,
    level: 0,
    progress: 0,
    progressTotal: 0,
    ticksToDowngrade: 0
};

var Structure_Rampart = function() {};
Structure_Rampart.prototype = {
    structureType: global.STRUCTURE_RAMPART,
    ticksToDecay: 0
};

var Structure_Road = function() { };
Structure_Road.prototype = {
    structureType: global.STRUCTURE_ROAD,
    ticksToDecay: 0
}

var Structure_Wall = function() { };
Structure_Wall.prototype = {
    structureType: global.STRUCTURE_WALL,
    ticksToLive: 0
};

var Structure_Storage = function() { };
Structure_Storage.prototype = {
    structureType: global.STRUCTURE_STORAGE,
    store: { energy: 0 },
    storeCapacity: 0,
    transferEnergy: function(target, amount) { }
};
var Structure = function () { };

Structure.prototype = {
	id: "",
	owner: null,
	room: null,
	pos: null,
	hits: 0,
	hitsMax: 0,
	structureType: "",
	my: true,
	destroy: function() { },
	notifyWhenAttacked: function(enabled) { }
};
var SurvivalInfo = function () { };

SurvivalInfo.prototype = {
    score: 0,
    timeToWave: 0,
    wave: 0
}
