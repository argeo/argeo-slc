(function(){var a="[Class ",
b="toString",
c="qx.Bootstrap",
d="]",
e="Class",
f=".";
qx={Bootstrap:{genericToString:function(){return a+this.classname+d;
},
createNamespace:function(g,
h){var j=g.split(f);
var k=window;
var l=j[0];
for(var m=0,
n=j.length-1;m<n;m++,
l=j[m]){if(!k[l]){k=k[l]={};
}else{k=k[l];
}}k[l]=h;
return l;
},
define:function(g,
o){if(!o){var o={statics:{}};
}var p;
var q=null;
if(o.members){p=o.construct||new Function;
var r=o.statics;
for(var s in r){p[s]=r[s];
}q=p.prototype;
var t=o.members;
for(var s in t){q[s]=t[s];
}}else{p=o.statics||{};
}var u=this.createNamespace(g,
p);
p.name=p.classname=g;
p.basename=u;
p.$$type=e;
if(!p.hasOwnProperty(b)){p.toString=this.genericToString;
}if(o.defer){o.defer(p,
q);
}qx.Bootstrap.$$registry[g]=o.statics;
}}};
qx.Bootstrap.define(c,
{statics:{LOADSTART:new Date,
createNamespace:qx.Bootstrap.createNamespace,
define:qx.Bootstrap.define,
genericToString:qx.Bootstrap.genericToString,
getByName:function(g){return this.$$registry[g];
},
$$registry:{}}});
})();
(function(){var a="qx.allowUrlSettings",
b="&",
c="qx.core.Setting",
d="qx.allowUrlVariants",
e="qxsetting",
f=":",
g=".";
qx.Bootstrap.define(c,
{statics:{__a:{},
define:function(h,
j){if(j===undefined){throw new Error('Default value of setting "'+h+'" must be defined!');
}
if(!this.__a[h]){this.__a[h]={};
}else if(this.__a[h].defaultValue!==undefined){throw new Error('Setting "'+h+'" is already defined!');
}this.__a[h].defaultValue=j;
},
get:function(h){var k=this.__a[h];
if(k===undefined){throw new Error('Setting "'+h+'" is not defined.');
}
if(k.value!==undefined){return k.value;
}return k.defaultValue;
},
__b:function(){if(window.qxsettings){for(var h in qxsettings){if((h.split(g)).length<2){throw new Error('Malformed settings key "'+h+'". Must be following the schema "namespace.key".');
}
if(!this.__a[h]){this.__a[h]={};
}this.__a[h].value=qxsettings[h];
}window.qxsettings=undefined;
try{delete window.qxsettings;
}catch(ex){}this.__c();
}},
__c:function(){if(this.get(a)!=true){return;
}var l=document.location.search.slice(1).split(b);
for(var m=0;m<l.length;m++){var n=l[m].split(f);
if(n.length!=3||n[0]!=e){continue;
}var h=n[1];
if(!this.__a[h]){this.__a[h]={};
}this.__a[h].value=decodeURIComponent(n[2]);
}}},
defer:function(o){o.define(a,
false);
o.define(d,
false);
o.__b();
}});
})();
(function(){var a="gecko",
b="[^\\.0-9]",
c="mshtml",
d="unknown",
e="Adobe Systems Incorporated",
f="webkit",
g="Gecko",
h="opera",
i="Apple Computer, Inc.",
j="0.0.0",
k=".",
l="qx.bom.client.Engine";
qx.Bootstrap.define(l,
{statics:{NAME:"",
FULLVERSION:"0.0.0",
VERSION:0.0,
OPERA:false,
WEBKIT:false,
GECKO:false,
MSHTML:false,
__d:function(){var m=d;
var n=j;
var o=navigator.userAgent;
if(window.opera){m=h;
this.OPERA=true;
if(/Opera[\s\/]([0-9\.]*)/.test(o)){n=RegExp.$1.substring(0,
3)+k+RegExp.$1.substring(3);
}else{throw new Error("Could not detect Opera version: "+o+"!");
}}else if(navigator.vendor&&(navigator.vendor===i||navigator.vendor===e)){m=f;
this.WEBKIT=true;
if(/AppleWebKit\/([^ ]+)/.test(o)){n=RegExp.$1;
var p=RegExp(b).exec(n);
if(p){n=n.slice(0,
p.index);
}}else{throw new Error("Could not detect Webkit version: "+o+"!");
}}else if(window.controllers&&navigator.product===g){m=a;
this.GECKO=true;
if(/rv\:([^\);]+)(\)|;)/.test(o)){n=RegExp.$1;
}else{throw new Error("Could not detect Gecko version: "+o+"!");
}}else if(navigator.cpuClass&&/MSIE\s+([^\);]+)(\)|;)/.test(o)){m=c;
n=RegExp.$1;
this.MSHTML=true;
}else{throw new Error("Unsupported client: "+o+"!");
}this.NAME=m;
this.FULLVERSION=n;
this.VERSION=parseFloat(n);
}},
defer:function(q){q.__d();
}});
})();
(function(){var a="on",
b="off",
c="default",
d="|",
e="object",
f="qxvariant",
g="qx.client",
h="qx.aspects",
j="qx.dynamicLocaleSwitch",
k="qx.debug",
m=":",
n="&",
o="qx.eventMonitorNoListeners",
p="qx.core.Variant",
q="gecko",
r="qx.compatibility",
s="$",
t="qx.allowUrlVariants",
u="qx.deprecationWarnings",
w="webkit",
x="opera",
y="mshtml";
qx.Bootstrap.define(p,
{statics:{__e:{},
__f:{},
compilerIsSet:function(){return true;
},
define:function(z,
A,
B){{};
if(!this.__e[z]){this.__e[z]={};
}else{}this.__e[z].allowedValues=A;
this.__e[z].defaultValue=B;
},
get:function(z){var C=this.__e[z];
{};
if(C.value!==undefined){return C.value;
}return C.defaultValue;
},
__g:function(){if(window.qxvariants){for(var z in qxvariants){{};
if(!this.__e[z]){this.__e[z]={};
}this.__e[z].value=qxvariants[z];
}window.qxvariants=undefined;
try{delete window.qxvariants;
}catch(ex){}this.__h(this.__e);
}},
__h:function(){if(qx.core.Setting.get(t)!=true){return;
}var D=document.location.search.slice(1).split(n);
for(var E=0;E<D.length;E++){var F=D[E].split(m);
if(F.length!=3||F[0]!=f){continue;
}var z=F[1];
if(!this.__e[z]){this.__e[z]={};
}this.__e[z].value=decodeURIComponent(F[2]);
}},
select:function(z,
G){{};
for(var F in G){if(this.isSet(z,
F)){return G[F];
}}
if(G[c]!==undefined){return G[c];
}{};
},
isSet:function(z,
H){var I=z+s+H;
if(this.__f[I]!==undefined){return this.__f[I];
}var J=false;
if(H.indexOf(d)<0){J=this.get(z)===H;
}else{var K=H.split(d);
for(var E=0,
L=K.length;E<L;E++){if(this.get(z)===K[E]){J=true;
break;
}}}this.__f[I]=J;
return J;
},
__i:function(M){return typeof M===e&&M!==null&&M instanceof Array;
},
__j:function(M){return typeof M===e&&M!==null&&!(M instanceof Array);
},
__k:function(N,
O){for(var E=0,
L=N.length;E<L;E++){if(N[E]==O){return true;
}}return false;
}},
defer:function(P){P.define(g,
[q,
y,
x,
w],
qx.bom.client.Engine.NAME);
P.define(k,
[a,
b],
a);
P.define(r,
[a,
b],
a);
P.define(o,
[a,
b],
b);
P.define(h,
[a,
b],
b);
P.define(u,
[a,
b],
a);
P.define(j,
[a,
b],
a);
P.__g();
}});
})();
(function(){var b='"',
c="valueOf",
d="toLocaleString",
e="isPrototypeOf",
f="",
g="toString",
h="qx.client",
j="qx.lang.Object",
k='\", "',
m="hasOwnProperty";
qx.Bootstrap.define(j,
{statics:{isEmpty:function(n){for(var o in n){return false;
}return true;
},
hasMinLength:function(n,
p){var q=0;
for(var o in n){if((++q)>=p){return true;
}}return false;
},
getLength:function(n){var q=0;
for(var o in n){q++;
}return q;
},
_shadowedKeys:[e,
m,
d,
g,
c],
getKeys:qx.core.Variant.select(h,
{"mshtml":function(n){var r=[];
for(var o in n){r.push(o);
}for(var q=0,
s=this._shadowedKeys,
t=s.length;q<t;q++){if(n.hasOwnProperty(s[q])){r.push(s[q]);
}}return r;
},
"default":function(n){var r=[];
for(var o in n){r.push(o);
}return r;
}}),
getKeysAsString:function(n){var u=qx.lang.Object.getKeys(n);
if(u.length==0){return f;
}return b+u.join(k)+b;
},
getValues:function(n){var r=[];
for(var o in n){r.push(n[o]);
}return r;
},
mergeWith:function(v,
w,
x){if(x===undefined){x=true;
}
for(var o in w){if(x||v[o]===undefined){v[o]=w[o];
}}return v;
},
carefullyMergeWith:function(v,
w){return qx.lang.Object.mergeWith(v,
w,
false);
},
merge:function(v,
y){var z=arguments.length;
for(var q=1;q<z;q++){qx.lang.Object.mergeWith(v,
arguments[q]);
}return v;
},
copy:function(w){var A={};
for(var o in w){A[o]=w[o];
}return A;
},
invert:function(n){var B={};
for(var o in n){B[n[o].toString()]=o;
}return B;
},
getKeyFromValue:function(C,
D){for(var o in C){if(C[o]===D){return o;
}}return null;
},
select:function(o,
n){return n[o];
},
fromArray:function(E){var C={};
for(var q=0,
t=E.length;q<t;q++){{};
C[E[q].toString()]=true;
}return C;
}}});
})();
(function(){var a="qx.core.Aspect",
b="before",
c="*",
d="static";
qx.Bootstrap.define(a,
{statics:{__l:[],
wrap:function(e,
f,
g){var h=[];
var j=[];
var k=this.__l;
var l;
for(var m=0;m<k.length;m++){l=k[m];
if((l.type==null||g==l.type||l.type==c)&&(l.name==null||e.match(l.name))){l.pos==-1?h.push(l.fcn):j.push(l.fcn);
}}
if(h.length===0&&j.length===0){return f;
}var n=function(){for(var m=0;m<h.length;m++){h[m].call(this,
e,
f,
g,
arguments);
}var o=f.apply(this,
arguments);
for(var m=0;m<j.length;m++){j[m].call(this,
e,
f,
g,
arguments,
o);
}return o;
};
if(g!==d){n.self=f.self;
n.base=f.base;
}f.wrapper=n;
n.original=f;
return n;
},
addAdvice:function(f,
p,
g,
q){this.__l.push({fcn:f,
pos:p===b?-1:1,
type:g,
name:q});
}}});
})();
(function(){var b="qx.aspects",
c=".",
d="on",
e="static",
f="[Class ",
g="]",
h="toString",
j="member",
k="$$init_",
m="destructor",
n="extend",
o="Class",
p="off",
q="qx.Class",
r="qx.event.type.Data";
qx.Bootstrap.define(q,
{statics:{define:function(s,
t){if(!t){var t={};
}if(t.include&&!(t.include instanceof Array)){t.include=[t.include];
}if(t.implement&&!(t.implement instanceof Array)){t.implement=[t.implement];
}if(!t.hasOwnProperty(n)&&!t.type){t.type=e;
}{};
var u=this.__q(s,
t.type,
t.extend,
t.statics,
t.construct,
t.destruct);
if(t.extend){if(t.properties){this.__s(u,
t.properties,
true);
}if(t.members){this.__u(u,
t.members,
true,
true,
false);
}if(t.events){this.__r(u,
t.events,
true);
}if(t.include){for(var v=0,
w=t.include.length;v<w;v++){this.__x(u,
t.include[v],
false);
}}}if(t.settings){for(var x in t.settings){qx.core.Setting.define(x,
t.settings[x]);
}}if(t.variants){for(var x in t.variants){qx.core.Variant.define(x,
t.variants[x].allowedValues,
t.variants[x].defaultValue);
}}if(t.implement){for(var v=0,
w=t.implement.length;v<w;v++){this.__w(u,
t.implement[v]);
}}{};
if(t.defer){t.defer.self=u;
t.defer(u,
u.prototype,
{add:function(s,
t){var y={};
y[s]=t;
qx.Class.__s(u,
y,
true);
}});
}},
isDefined:function(s){return this.getByName(s)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.$$registry);
},
getByName:function(s){return this.$$registry[s];
},
include:function(u,
z){{};
qx.Class.__x(u,
z,
false);
},
patch:function(u,
z){{};
qx.Class.__x(u,
z,
true);
},
isSubClassOf:function(u,
A){if(!u){return false;
}
if(u==A){return true;
}
if(u.prototype instanceof A){return true;
}return false;
},
getPropertyDefinition:function(u,
s){while(u){if(u.$$properties&&u.$$properties[s]){return u.$$properties[s];
}u=u.superclass;
}return null;
},
getProperties:function(u){var B=[];
while(u){if(u.$$properties){B.push.apply(B,
qx.lang.Object.getKeys(u.$$properties));
}u=u.superclass;
}return B;
},
getByProperty:function(u,
s){while(u){if(u.$$properties&&u.$$properties[s]){return u;
}u=u.superclass;
}return null;
},
hasProperty:function(u,
s){return !!this.getPropertyDefinition(u,
s);
},
getEventType:function(u,
s){var u=u.constructor;
while(u.superclass){if(u.$$events&&u.$$events[s]!==undefined){return u.$$events[s];
}u=u.superclass;
}return null;
},
supportsEvent:function(u,
s){return !!this.getEventType(u,
s);
},
hasOwnMixin:function(u,
z){return u.$$includes&&u.$$includes.indexOf(z)!==-1;
},
getByMixin:function(u,
z){var B,
v,
w;
while(u){if(u.$$includes){B=u.$$flatIncludes;
for(v=0,
w=B.length;v<w;v++){if(B[v]===z){return u;
}}}u=u.superclass;
}return null;
},
getMixins:function(u){var B=[];
while(u){if(u.$$includes){B.push.apply(B,
u.$$flatIncludes);
}u=u.superclass;
}return B;
},
hasMixin:function(u,
z){return !!this.getByMixin(u,
z);
},
hasOwnInterface:function(u,
C){return u.$$implements&&u.$$implements.indexOf(C)!==-1;
},
getByInterface:function(u,
C){var B,
v,
w;
while(u){if(u.$$implements){B=u.$$flatImplements;
for(v=0,
w=B.length;v<w;v++){if(B[v]===C){return u;
}}}u=u.superclass;
}return null;
},
getInterfaces:function(u){var B=[];
while(u){if(u.$$implements){B.push.apply(B,
u.$$flatImplements);
}u=u.superclass;
}return B;
},
hasInterface:function(u,
C){return !!this.getByInterface(u,
C);
},
implementsInterface:function(u,
C){if(this.hasInterface(u,
C)){return true;
}
try{qx.Interface.assert(u,
C,
false);
return true;
}catch(ex){}return false;
},
getInstance:function(){if(!this.$$instance){this.$$allowconstruct=true;
this.$$instance=new this;
delete this.$$allowconstruct;
}return this.$$instance;
},
genericToString:function(){return f+this.classname+g;
},
$$registry:qx.Bootstrap.$$registry,
__m:null,
__n:null,
__o:function(){},
__p:function(){},
__q:function(s,
D,
E,
F,
G,
H){var u;
if(!E&&qx.core.Variant.isSet(b,
p)){u=F||{};
}else{u={};
if(E){if(!G){G=this.__y();
}u=this.__A(G,
s,
D);
}if(F){var x;
for(var v=0,
I=qx.lang.Object.getKeys(F),
w=I.length;v<w;v++){x=I[v];
if(qx.core.Variant.isSet(b,
d)){var J=F[x];
if(J instanceof Function){J=qx.core.Aspect.wrap(s+c+x,
J,
e);
}u[x]=J;
}else{u[x]=F[x];
}}}}var K=qx.Bootstrap.createNamespace(s,
u,
false);
u.name=u.classname=s;
u.basename=K;
u.$$type=o;
if(D){u.$$classtype=D;
}if(!u.hasOwnProperty(h)){u.toString=this.genericToString;
}
if(E){var L=E.prototype;
var M=this.__z();
M.prototype=L;
var N=new M;
u.prototype=N;
N.name=N.classname=s;
N.basename=K;
G.base=u.superclass=E;
G.self=u.constructor=N.constructor=u;
if(H){if(qx.core.Variant.isSet(b,
d)){H=qx.core.Aspect.wrap(s,
H,
m);
}u.$$destructor=H;
}}this.$$registry[s]=u;
return u;
},
__r:function(u,
O,
P){var x,
x;
if(u.$$events){for(var x in O){u.$$events[x]=O[x];
}}else{u.$$events=O;
}},
__s:function(u,
y,
P){var t;
if(P===undefined){P=false;
}var Q=!!u.$$propertiesAttached;
for(var s in y){t=y[s];
{};
t.name=s;
if(!t.refine){if(u.$$properties===undefined){u.$$properties={};
}u.$$properties[s]=t;
}if(t.init!==undefined){u.prototype[k+s]=t.init;
}if(t.event!==undefined){var R={};
R[t.event]=r;
this.__r(u,
R,
P);
}if(t.inheritable){qx.core.Property.$$inheritable[s]=true;
}if(Q){qx.core.Property.attachMethods(u,
s,
t);
}}},
__t:null,
__u:function(u,
S,
P,
T,
U){var N=u.prototype;
var x,
V;
for(var v=0,
I=qx.lang.Object.getKeys(S),
w=I.length;v<w;v++){x=I[v];
V=S[x];
{};
if(T!==false&&V instanceof Function&&V.$$type==null){if(U==true){V=this.__v(V,
N[x]);
}else{if(N[x]){V.base=N[x];
}V.self=u;
}
if(qx.core.Variant.isSet(b,
d)){V=qx.core.Aspect.wrap(u.classname+c+x,
V,
j);
}}N[x]=V;
}},
__v:function(V,
T){if(T){return function(){var W=V.base;
V.base=T;
var X=V.apply(this,
arguments);
V.base=W;
return X;
};
}else{return V;
}},
__w:function(u,
C){{};
var B=qx.Interface.flatten([C]);
if(u.$$implements){u.$$implements.push(C);
u.$$flatImplements.push.apply(u.$$flatImplements,
B);
}else{u.$$implements=[C];
u.$$flatImplements=B;
}},
__x:function(u,
z,
P){{};
if(this.hasMixin(u,
z)){qx.log.Logger.warn('Mixin "'+z.name+'" is already included into Class "'+u.classname+'" by class: '+this.getByMixin(u,
z).classname+'!');
return;
}var B=qx.Mixin.flatten([z]);
var Y;
for(var v=0,
w=B.length;v<w;v++){Y=B[v];
if(Y.$$events){this.__r(u,
Y.$$events,
P);
}if(Y.$$properties){this.__s(u,
Y.$$properties,
P);
}if(Y.$$members){this.__u(u,
Y.$$members,
P,
P,
P);
}}if(u.$$includes){u.$$includes.push(z);
u.$$flatIncludes.push.apply(u.$$flatIncludes,
B);
}else{u.$$includes=[z];
u.$$flatIncludes=B;
}},
__y:function(){function ba(){arguments.callee.base.apply(this,
arguments);
}return ba;
},
__z:function(){return function(){};
},
__A:function(G,
s,
D){var bb=function(){var u=arguments.callee.constructor;
{};
if(!u.$$propertiesAttached){qx.core.Property.attach(u);
}var X=u.$$original.apply(this,
arguments);
if(u.$$includes){var bc=u.$$flatIncludes;
for(var v=0,
w=bc.length;v<w;v++){if(bc[v].$$constructor){bc[v].$$constructor.apply(this,
arguments);
}}}if(this.classname===s.classname){this.$$initialized=true;
}return X;
};
if(qx.core.Variant.isSet("qx.aspects",
"on")){var bd=qx.core.Aspect.wrap(s,
bb,
"constructor");
bb.$$original=G;
bb.constructor=bd;
bb=bd;
}if(D==="singleton"){bb.getInstance=this.getInstance;
}bb.$$original=G;
G.wrapper=bb;
return bb;
}},
defer:function(F){for(var be in qx.Bootstrap.$$registry){var F=qx.Bootstrap.$$registry[be];
for(var x in F){if(F[x] instanceof Function){F[x]=qx.core.Aspect.wrap(be+c+x,
F[x],
e);
}}}}});
})();
(function(){var a="qx.client",
b="on",
c="qx.bom.Event",
d="mousedown",
f="mouseover";
qx.Bootstrap.define(c,
{statics:{addNativeListener:qx.core.Variant.select(a,
{"mshtml":function(g,
h,
i){g.attachEvent(b+h,
i);
},
"default":function(g,
h,
i){g.addEventListener(h,
i,
false);
}}),
removeNativeListener:qx.core.Variant.select(a,
{"mshtml":function(g,
h,
i){g.detachEvent(b+h,
i);
},
"default":function(g,
h,
i){g.removeEventListener(h,
i,
false);
}}),
getTarget:function(j){return j.target||j.srcElement;
},
getRelatedTarget:qx.core.Variant.select(a,
{"mshtml":function(j){if(j.type===f){return j.fromEvent;
}else{return j.toElement;
}},
"default":function(j){return j.relatedTarget;
}}),
preventDefault:qx.core.Variant.select(a,
{"gecko":function(j){if(qx.bom.client.Engine.VERSION>=1.9&&j.type==d&&j.button==2){return;
}j.preventDefault();
try{j.keyCode=0;
}catch(j){}},
"mshtml":function(j){try{j.keyCode=0;
}catch(j){}j.returnValue=false;
},
"default":function(j){j.preventDefault();
}}),
stopPropagation:function(j){if(j.stopPropagation){j.stopPropagation();
}j.cancelBubble=true;
}}});
})();
(function(){var a="|bubble",
b="|capture",
c="_",
d="unload",
e="UNKNOWN_",
f="DOM_",
g="capture",
h="WIN_",
j='|',
k="qx.event.Manager",
m="QX_";
qx.Bootstrap.define(k,
{construct:function(n){this.__B=n;
this.__C=qx.lang.Function.bind(this.dispose,
this);
qx.bom.Event.addNativeListener(n,
d,
this.__C);
this.__D={};
this.__E={};
this.__F={};
this.__G={};
},
members:{dispose:function(){qx.bom.Event.removeNativeListener(this.__B,
d,
this.__C);
qx.event.Registration.removeManager(this);
this.__D=this.__B=this.__E=this.__F=this.__C=this.__G=null;
},
getWindow:function(){return this.__B;
},
getHandler:function(o){var p=this.__E[o.classname];
if(p){return p;
}return this.__E[o.classname]=new o(this);
},
getDispatcher:function(o){var q=this.__F[o.classname];
if(q){return q;
}return this.__F[o.classname]=new o(this);
},
getListeners:function(r,
s,
t){var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u];
if(!v){return null;
}var w=s+(t?b:a);
var x=v[w];
return x?x.concat():null;
},
hasListener:function(r,
s,
t){{};
var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u];
if(!v){return false;
}var w=s+(t?b:a);
var x=v[w];
if(!x){return false;
}return x.length>0;
},
importListeners:function(r,
y){{};
var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u]={};
for(var z in y){var A=y[z];
var w=A.type+(A.capture?b:a);
var x=v[w];
if(!x){x=v[w]=[];
this.__H(r,
A.type,
A.capture);
}x.push({handler:A.listener,
context:A.self});
}},
addListener:function(r,
s,
B,
C,
t){var D;
var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u];
if(!v){v=this.__D[u]={};
}var w=s+(t?b:a);
var x=v[w];
if(!x){x=v[w]=[];
}if(x.length===0){this.__H(r,
s,
t);
}x.push({handler:B,
context:C});
},
_findHandler:function(r,
s){var E;
var F=false;
var G=false;
var H=false;
if(r.nodeType===1){F=true;
E=f+r.tagName.toLowerCase()+c+s;
}else if(r==this.__B){G=true;
E=h+s;
}else if(r.classname){H=true;
E=m+r.classname+c+s;
}else{E=e+r+c+s;
}var I=this.__G;
if(I[E]){return I[E];
}var J=qx.event.Registration.getHandlers();
var K;
for(var L=0,
M=J.length;L<M;L++){var o=J[L];
var N=o.SUPPORTED_TYPES;
if(N&&!N[s]){continue;
}var O=qx.event.IEventHandler;
var P=o.TARGET_CHECK;
if(P){if(P===O.TARGET_DOMNODE&&!F){continue;
}else if(P===O.TARGET_WINDOW&&!G){continue;
}else if(P===O.TARGET_OBJECT&&!H){continue;
}}K=this.getHandler(J[L]);
if(o.IGNORE_CAN_HANDLE||K.canHandleEvent(r,
s)){I[E]=K;
return K;
}}return null;
},
__H:function(r,
s,
t){var p=this._findHandler(r,
s);
if(p){p.registerEvent(r,
s,
t);
return;
}{};
},
removeListener:function(r,
s,
B,
C,
t){var D;
var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u];
if(!v){return false;
}var w=s+(t?b:a);
var x=v[w];
if(!x){return false;
}
for(var L=0,
M=x.length;L<M;L++){var Q=x[L];
if(Q.handler===B&&Q.context===C){qx.lang.Array.removeAt(x,
L);
if(x.length==0){this.__I(r,
s,
t);
}return true;
}}return false;
},
removeAllListeners:function(r){var u=qx.core.ObjectRegistry.toHashCode(r);
var v=this.__D[u];
if(!v){return false;
}var R,
s,
t;
for(var w in v){if(v[w].length>0){R=w.split(j);
s=R[0];
t=R[1]===g;
this.__I(r,
s,
t);
}}delete this.__D[u];
return true;
},
__I:function(r,
s,
t){var p=this._findHandler(r,
s);
if(p){p.unregisterEvent(r,
s,
t);
return;
}{};
},
dispatchEvent:function(r,
S){var D;
var s=S.getType();
if(!S.getBubbles()&&!this.hasListener(r,
s)){qx.event.Pool.getInstance().poolObject(S);
return true;
}
if(!S.getTarget()){S.setTarget(r);
}var J=qx.event.Registration.getDispatchers();
var K;
var T=false;
for(var L=0,
M=J.length;L<M;L++){K=this.getDispatcher(J[L]);
if(K.canDispatchEvent(r,
S,
s)){K.dispatchEvent(r,
S,
s);
T=true;
break;
}}
if(!T){qx.log.Logger.error(this,
"No dispatcher can handle event of type "+s+" on "+r);
return true;
}var U=S.getDefaultPrevented();
qx.event.Pool.getInstance().poolObject(S);
return !U;
}}});
})();
(function(){var b="qx.dom.Node",
c="qx.client",
d="",
e="object";
qx.Class.define(b,
{statics:{ELEMENT:1,
ATTRIBUTE:2,
TEXT:3,
CDATA_SECTION:4,
ENTITY_REFERENCE:5,
ENTITY:6,
PROCESSING_INSTRUCTION:7,
COMMENT:8,
DOCUMENT:9,
DOCUMENT_TYPE:10,
DOCUMENT_FRAGMENT:11,
NOTATION:12,
getDocument:function(f){if(this.isDocument(f)){return f;
}return f.ownerDocument||f.document||null;
},
getWindow:qx.core.Variant.select(c,
{"mshtml":function(f){return this.getDocument(f).parentWindow;
},
"default":function(f){return this.getDocument(f).defaultView;
}}),
getDocumentElement:function(f){return this.getDocument(f).documentElement;
},
getBodyElement:function(f){return this.getDocument(f).body;
},
isElement:function(f){return !!(f&&f.nodeType===qx.dom.Node.ELEMENT);
},
isDocument:function(f){return !!(f&&f.nodeType===qx.dom.Node.DOCUMENT);
},
isText:function(f){return !!(f&&f.nodeType===qx.dom.Node.TEXT);
},
isWindow:function(g){return !!(typeof g===e&&g&&g.Array);
},
getText:function(f){if(!f||!f.nodeType){return null;
}
switch(f.nodeType){case 1:var h,
j=[],
k=f.childNodes,
l=k.length;
for(h=0;h<l;h++){j[h]=this.getText(k[h]);
}return j.join(d);
case 2:return f.nodeValue;
break;
case 3:return f.nodeValue;
break;
}return null;
}}});
})();
(function(){var b="qx.lang.Array",
c="qx.client",
d="mshtml";
qx.Bootstrap.define(b,
{statics:{fromArguments:function(e,
f){return Array.prototype.slice.call(e,
f||0);
},
fromCollection:function(g){if(qx.core.Variant.isSet(c,
d)){if(g.item){var h=[];
for(var j=0,
k=g.length;j<k;j++){h[j]=g[j];
}return h;
}}return Array.prototype.slice.call(g,
0);
},
fromShortHand:function(m){var n=m.length;
var o=qx.lang.Array.copy(m);
switch(n){case 1:o[1]=o[2]=o[3]=o[0];
break;
case 2:o[2]=o[0];
case 3:o[3]=o[1];
}return o;
},
copy:function(h){return h.concat();
},
clone:function(h){return h.concat();
},
getLast:function(h){return h[h.length-1];
},
getFirst:function(h){return h[0];
},
insertAt:function(h,
p,
j){h.splice(j,
0,
p);
return h;
},
insertBefore:function(h,
p,
q){var j=h.indexOf(q);
if(j==-1){h.push(p);
}else{h.splice(j,
0,
p);
}return h;
},
insertAfter:function(h,
p,
q){var j=h.indexOf(q);
if(j==-1||j==(h.length-1)){h.push(p);
}else{h.splice(j+1,
0,
p);
}return h;
},
removeAt:function(h,
j){return h.splice(j,
1)[0];
},
removeAll:function(h){return h.length=0;
},
append:function(h,
r){{};
Array.prototype.push.apply(h,
r);
return h;
},
remove:function(h,
p){var j=h.indexOf(p);
if(j!=-1){h.splice(j,
1);
return p;
}},
contains:function(h,
p){return h.indexOf(p)!==-1;
},
equals:function(s,
t){var u=s.length;
if(u!==t.length){return false;
}
for(var j=0;j<u;j++){if(s[j]!==t[j]){return false;
}}return true;
},
sum:function(h){var o=0;
for(var j=0,
k=h.length;j<k;j++){o+=h[j];
}return o;
},
max:function(h){{};
var j,
n=h.length,
o=h[0];
for(j=1;j<n;j++){if(h[j]>o){o=h[j];
}}return o===undefined?null:o;
},
min:function(h){{};
var j,
n=h.length,
o=h[0];
for(j=1;j<n;j++){if(h[j]<o){o=h[j];
}}return o===undefined?null:o;
}}});
})();
(function(){var a=":",
b=":constructor",
c='anonymous',
d="anonymous: ",
e="qx.lang.Function",
f=":constructor wrapper";
qx.Bootstrap.define(e,
{statics:{getCaller:function(g){return g.caller?g.caller.callee:g.callee.caller;
},
getName:function(h){if(h.$$original){return h.classname+f;
}
if(h.wrapper){return h.wrapper.classname+b;
}
if(h.classname){return h.classname+b;
}
if(h.mixin){for(var i in h.mixin.$$members){if(h.mixin.$$members[i]==h){return h.mixin.name+a+i;
}}for(var i in h.mixin){if(h.mixin[i]==h){return h.mixin.name+a+i;
}}}
if(h.self){var j=h.self.constructor;
if(j){for(var i in j.prototype){if(j.prototype[i]==h){return j.classname+a+i;
}}for(var i in j){if(j[i]==h){return j.classname+a+i;
}}}}var k=h.toString().match(/(function\s*\w*\(.*?\))/);
if(k&&k.length>=1&&k[1]){return k[1];
}var k=h.toString().match(/(function\s*\(.*?\))/);
if(k&&k.length>=1&&k[1]){return d+k[1];
}return c;
},
globalEval:function(l){if(window.execScript){return window.execScript(l);
}else{return eval.call(window,
l);
}},
returnTrue:function(){return true;
},
returnFalse:function(){return false;
},
returnNull:function(){return null;
},
returnThis:function(){return this;
},
returnZero:function(){return 0;
},
create:function(m,
n){{};
if(!n){return m;
}if(!(n.self||n.args||n.delay!=null||n.periodical!=null||n.attempt)){return m;
}return function(o){var g=qx.lang.Array.fromArguments(arguments);
if(n.args){g=n.args.concat(g);
}
if(n.delay||n.periodical){var p=function(){return m.apply(n.self||this,
g);
};
if(n.delay){return setTimeout(p,
n.delay);
}
if(n.periodical){return setInterval(p,
n.periodical);
}}else if(n.attempt){var q=false;
try{q=m.apply(n.self||this,
g);
}catch(ex){}return q;
}else{return m.apply(n.self||this,
g);
}};
},
bind:function(m,
r,
s){return this.create(m,
{self:r,
args:s!==undefined?qx.lang.Array.fromArguments(arguments,
2):null});
},
curry:function(m,
s){return this.create(m,
{args:s!==undefined?qx.lang.Array.fromArguments(arguments,
1):null});
},
listener:function(m,
r,
s){if(s===undefined){return function(o){return m.call(r||this,
o||window.event);
};
}else{var t=qx.lang.Array.fromArguments(arguments,
2);
return function(o){var g=[o||window.event];
g.push.apply(g,
t);
m.apply(r||this,
g);
};
}},
attempt:function(m,
r,
s){return this.create(m,
{self:r,
attempt:true,
args:s!==undefined?qx.lang.Array.fromArguments(arguments,
2):null})();
},
delay:function(m,
u,
r,
s){return this.create(m,
{delay:u,
self:r,
args:s!==undefined?qx.lang.Array.fromArguments(arguments,
3):null})();
},
periodical:function(m,
v,
r,
s){return this.create(m,
{periodical:v,
self:r,
args:s!==undefined?qx.lang.Array.fromArguments(arguments,
3):null})();
}}});
})();
(function(){var c="qx.event.Registration";
qx.Bootstrap.define(c,
{statics:{__J:{},
getManager:function(d){if(qx.dom.Node.isWindow(d)){var e=d;
}else if(qx.dom.Node.isElement(d)){var e=qx.dom.Node.getWindow(d);
}else{var e=window;
}var f=qx.core.ObjectRegistry.toHashCode(e);
var g=this.__J[f];
if(!g){g=new qx.event.Manager(e);
this.__J[f]=g;
}return g;
},
removeManager:function(h){var f=qx.core.ObjectRegistry.toHashCode(h.getWindow());
delete this.__J[f];
},
addListener:function(d,
i,
j,
k,
l){this.getManager(d).addListener(d,
i,
j,
k,
l);
},
removeListener:function(d,
i,
j,
k,
l){this.getManager(d).removeListener(d,
i,
j,
k,
l);
},
removeAllListeners:function(d){this.getManager(d).removeAllListeners(d);
},
hasListener:function(d,
i,
l){return this.getManager(d).hasListener(d,
i,
l);
},
createEvent:function(i,
m,
n){{};
if(m==null){m=qx.event.type.Event;
}var o=qx.event.Pool.getInstance().getObject(m);
if(!o){return;
}n?o.init.apply(o,
n):o.init();
if(i){o.setType(i);
}return o;
},
dispatchEvent:function(d,
p){return this.getManager(d).dispatchEvent(d,
p);
},
fireEvent:function(d,
i,
m,
n){var q;
var r=this.createEvent(i,
m||null,
n);
return this.getManager(d).dispatchEvent(d,
r);
},
fireNonBubblingEvent:function(d,
i,
m,
n){{};
var h=this.getManager(d);
if(!h.hasListener(d,
i,
false)){return true;
}var r=this.createEvent(i,
m||null,
n);
return h.dispatchEvent(d,
r);
},
PRIORITY_FIRST:-32000,
PRIORITY_NORMAL:0,
PRIORITY_LAST:32000,
__K:[],
addHandler:function(s){{};
this.__K.push(s);
this.__K.sort(function(t,
u){return t.PRIORITY-u.PRIORITY;
});
},
getHandlers:function(){return this.__K;
},
__L:[],
addDispatcher:function(v,
w){{};
this.__L.push(v);
this.__L.sort(function(t,
u){return t.PRIORITY-u.PRIORITY;
});
},
getDispatchers:function(){return this.__L;
}}});
})();
(function(){var b=';',
c='computed=this.',
d='=value;',
e='this.',
f='if(this.',
g='delete this.',
h='!==undefined)',
j="set",
k="setRuntime",
m="setThemed",
n='}',
o="init",
p='else if(this.',
q='return this.',
r="boolean",
s='!==undefined){',
t="string",
u="resetThemed",
v='=true;',
w="resetRuntime",
x="reset",
y='old=this.',
z="refresh",
A='if(old===undefined)old=null;',
B='else ',
C=' of an instance of ',
D='if(old===computed)return value;',
E='old=computed=this.',
F=' is not (yet) ready!");',
G='!==inherit){',
H=")}",
I="': ",
J=" of class ",
K='if(computed===undefined)computed=null;',
L='return value;',
M='===value)return value;',
N='else{',
O='if(init==qx.core.Property.$$inherit)throw new Error("Inheritable property ',
P='return init;',
Q='var init=this.',
R="')){",
S="if(reg.hasListener(this, '",
T='else this.',
U="Error in property ",
V='value=this.',
W='var a=this._getChildren();if(a)for(var i=0,l=a.length;i<l;i++){',
X='if((computed===undefined||computed===inherit)&&',
Y='if(init==qx.core.Property.$$inherit)init=null;',
ba="reg.fireEvent(this, '",
bb=';}',
bc='===undefined)return;',
bd='if(a[i].',
be="', qx.event.type.Data, [computed, old]",
bf='");',
bg='var computed, old=this.',
bh='(value);',
bi=" in method ",
bj='throw new Error("Property ',
bk='(backup);',
bl='var inherit=prop.$$inherit;',
bm='return null;',
bn="var reg=qx.event.Registration;",
bo='(computed, old, "',
bp='",value);',
bq='computed=value;',
br='if(computed===undefined||computed==inherit)computed=null;',
bs='var prop=qx.core.Property;',
bt=')a[i].',
bu='computed=undefined;delete this.',
bv='if(computed===inherit){',
bw="inherit",
bx='var pa=this.getLayoutParent();if(pa)computed=pa.',
by=" with incoming value '",
bz='){',
bA='!==undefined&&',
bB='else if(computed===undefined)',
bC='if(value===undefined)prop.error(this,2,"',
bD='var computed, old;',
bE='if(computed===undefined||computed===inherit){',
bF='","',
bG='var backup=computed;',
bH='}else{',
bI='=computed;',
bJ="object",
bK="qx.core.Property";
qx.Class.define(bK,
{statics:{__M:{"Boolean":'qx.core.Assert.assertBoolean(value, msg) || true',
"String":'qx.core.Assert.assertString(value, msg) || true',
"Number":'qx.core.Assert.assertNumber(value, msg) || true',
"Integer":'qx.core.Assert.assertInteger(value, msg) || true',
"PositiveNumber":'qx.core.Assert.assertPositiveNumber(value, msg) || true',
"PositiveInteger":'qx.core.Assert.assertPositiveInteger(value, msg) || true',
"Error":'qx.core.Assert.assertInstance(value, Error, msg) || true',
"RegExp":'qx.core.Assert.assertInstance(value, RegExp, msg) || true',
"Object":'qx.core.Assert.assertObject(value, msg) || true',
"Array":'qx.core.Assert.assertArray(value, msg) || true',
"Map":'qx.core.Assert.assertMap(value, msg) || true',
"Function":'qx.core.Assert.assertFunction(value, msg) || true',
"Date":'qx.core.Assert.assertInstance(value, Date, msg) || true',
"Node":'value !== null && value.nodeType !== undefined',
"Element":'value !== null && value.nodeType === 1 && value.attributes',
"Document":'value !== null && value.nodeType === 9 && value.documentElement',
"Window":'value !== null && value.document',
"Event":'value !== null && value.type !== undefined',
"Class":'value !== null && value.$$type === "Class"',
"Mixin":'value !== null && value.$$type === "Mixin"',
"Interface":'value !== null && value.$$type === "Interface"',
"Theme":'value !== null && value.$$type === "Theme"',
"Color":'(typeof value === "string" || value instanceof String) && qx.util.ColorUtil.isValidPropertyValue(value)',
"Decorator":'value !== null && qx.theme.manager.Decoration.getInstance().isValidPropertyValue(value)',
"Font":'value !== null && qx.theme.manager.Font.getInstance().isDynamic(value)'},
__N:{"Object":true,
"Array":true,
"Map":true,
"Function":true,
"Date":true,
"Node":true,
"Element":true,
"Document":true,
"Window":true,
"Event":true,
"Class":true,
"Mixin":true,
"Interface":true,
"Theme":true,
"Font":true,
"Decorator":true},
$$inherit:bw,
$$store:{runtime:{},
user:{},
theme:{},
inherit:{},
init:{},
useinit:{}},
$$method:{get:{},
set:{},
reset:{},
init:{},
refresh:{},
setRuntime:{},
resetRuntime:{},
setThemed:{},
resetThemed:{}},
$$allowedKeys:{name:t,
dispose:r,
inheritable:r,
nullable:r,
themeable:r,
refine:r,
init:null,
apply:t,
event:t,
check:null,
transform:t,
deferredInit:r},
$$allowedGroupKeys:{name:t,
group:bJ,
mode:t,
themeable:r},
$$inheritable:{},
refresh:function(bL){var bM=bL.getLayoutParent();
if(bM){var bN=bL.constructor;
var bO=this.$$store.inherit;
var bP=this.$$store.init;
var bQ=this.$$method.refresh;
var bR;
var bS;
{};
while(bN){bR=bN.$$properties;
if(bR){for(var bT in this.$$inheritable){if(bR[bT]&&bL[bQ[bT]]){bS=bM[bO[bT]];
if(bS===undefined){bS=bM[bP[bT]];
}{};
bL[bQ[bT]](bS);
}}}bN=bN.superclass;
}}},
attach:function(bN){var bR=bN.$$properties;
if(bR){for(var bT in bR){this.attachMethods(bN,
bT,
bR[bT]);
}}bN.$$propertiesAttached=true;
},
attachMethods:function(bN,
bT,
bU){bU.group?this.__O(bN,
bU,
bT):this.__P(bN,
bU,
bT);
},
__O:function(bN,
bU,
bT){var bV=qx.lang.String.firstUp(bT);
var bW=bN.prototype;
var bX=bU.themeable===true;
{};
var bY=[];
var ca=[];
if(bX){var cb=[];
var cc=[];
}var cd="var a=arguments[0] instanceof Array?arguments[0]:arguments;";
bY.push(cd);
if(bX){cb.push(cd);
}
if(bU.mode=="shorthand"){var ce="a=qx.lang.Array.fromShortHand(qx.lang.Array.fromArguments(a));";
bY.push(ce);
if(bX){cb.push(ce);
}}
for(var cf=0,
cg=bU.group,
ch=cg.length;cf<ch;cf++){{};
bY.push("this.",
this.$$method.set[cg[cf]],
"(a[",
cf,
"]);");
ca.push("this.",
this.$$method.reset[cg[cf]],
"();");
if(bX){{};
cb.push("this.",
this.$$method.setThemed[cg[cf]],
"(a[",
cf,
"]);");
cc.push("this.",
this.$$method.resetThemed[cg[cf]],
"();");
}}this.$$method.set[bT]="set"+bV;
bW[this.$$method.set[bT]]=new Function(bY.join(""));
this.$$method.reset[bT]="reset"+bV;
bW[this.$$method.reset[bT]]=new Function(ca.join(""));
if(bX){this.$$method.setThemed[bT]="setThemed"+bV;
bW[this.$$method.setThemed[bT]]=new Function(cb.join(""));
this.$$method.resetThemed[bT]="resetThemed"+bV;
bW[this.$$method.resetThemed[bT]]=new Function(cc.join(""));
}},
__P:function(bN,
bU,
bT){var bV=qx.lang.String.firstUp(bT);
var bW=bN.prototype;
{};
if(bU.dispose===undefined&&typeof bU.check==="string"){bU.dispose=this.__N[bU.check]||qx.Class.isDefined(bU.check)||qx.Interface.isDefined(bU.check);
}var ci=this.$$method;
var cj=this.$$store;
cj.runtime[bT]="$$runtime_"+bT;
cj.user[bT]="$$user_"+bT;
cj.theme[bT]="$$theme_"+bT;
cj.init[bT]="$$init_"+bT;
cj.inherit[bT]="$$inherit_"+bT;
cj.useinit[bT]="$$useinit_"+bT;
ci.get[bT]="get"+bV;
bW[ci.get[bT]]=function(){return qx.core.Property.executeOptimizedGetter(this,
bN,
bT,
"get");
};
ci.set[bT]="set"+bV;
bW[ci.set[bT]]=function(bS){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"set",
arguments);
};
ci.reset[bT]="reset"+bV;
bW[ci.reset[bT]]=function(){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"reset");
};
if(bU.inheritable||bU.apply||bU.event||bU.deferredInit){ci.init[bT]="init"+bV;
bW[ci.init[bT]]=function(bS){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"init",
arguments);
};
}
if(bU.inheritable){ci.refresh[bT]="refresh"+bV;
bW[ci.refresh[bT]]=function(bS){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"refresh",
arguments);
};
}ci.setRuntime[bT]="setRuntime"+bV;
bW[ci.setRuntime[bT]]=function(bS){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"setRuntime",
arguments);
};
ci.resetRuntime[bT]="resetRuntime"+bV;
bW[ci.resetRuntime[bT]]=function(){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"resetRuntime");
};
if(bU.themeable){ci.setThemed[bT]="setThemed"+bV;
bW[ci.setThemed[bT]]=function(bS){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"setThemed",
arguments);
};
ci.resetThemed[bT]="resetThemed"+bV;
bW[ci.resetThemed[bT]]=function(){return qx.core.Property.executeOptimizedSetter(this,
bN,
bT,
"resetThemed");
};
}
if(bU.check==="Boolean"){bW["toggle"+bV]=new Function("return this."+ci.set[bT]+"(!this."+ci.get[bT]+"())");
bW["is"+bV]=new Function("return this."+ci.get[bT]+"()");
}},
__Q:{0:'Could not change or apply init value after constructing phase!',
1:'Requires exactly one argument!',
2:'Undefined value is not allowed!',
3:'Does not allow any arguments!',
4:'Null value is not allowed!',
5:'Is invalid!'},
error:function(ck,
cl,
cm,
cn,
bS){var co=ck.constructor.classname;
var cp=U+cm+J+co+bi+this.$$method[cn][cm]+by+bS+I;
throw new Error(cp+(this.__Q[cl]||"Unknown reason: "+cl));
},
__R:function(cq,
bW,
bT,
cn,
cr,
cs){var cj=this.$$method[cn][bT];
{bW[cj]=new Function("value",
cr.join(""));
};
if(qx.core.Variant.isSet("qx.aspects",
"on")){bW[cj]=qx.core.Aspect.wrap(cq.classname+"."+cj,
bW[cj],
"property");
}if(cs===undefined){return cq[cj]();
}else{return cq[cj](cs[0]);
}},
executeOptimizedGetter:function(cq,
bN,
bT,
cn){var bU=bN.$$properties[bT];
var bW=bN.prototype;
var cr=[];
var cj=this.$$store;
cr.push(f,
cj.runtime[bT],
h);
cr.push(q,
cj.runtime[bT],
b);
if(bU.inheritable){cr.push(p,
cj.inherit[bT],
h);
cr.push(q,
cj.inherit[bT],
b);
cr.push(B);
}cr.push(f,
cj.user[bT],
h);
cr.push(q,
cj.user[bT],
b);
if(bU.themeable){cr.push(p,
cj.theme[bT],
h);
cr.push(q,
cj.theme[bT],
b);
}
if(bU.deferredInit&&bU.init===undefined){cr.push(p,
cj.init[bT],
h);
cr.push(q,
cj.init[bT],
b);
}cr.push(B);
if(bU.init!==undefined){if(bU.inheritable){cr.push(Q,
cj.init[bT],
b);
if(bU.nullable){cr.push(Y);
}else if(bU.init!==undefined){cr.push(q,
cj.init[bT],
b);
}else{cr.push(O,
bT,
C,
bN.classname,
F);
}cr.push(P);
}else{cr.push(q,
cj.init[bT],
b);
}}else if(bU.inheritable||bU.nullable){cr.push(bm);
}else{cr.push(bj,
bT,
C,
bN.classname,
F);
}return this.__R(cq,
bW,
bT,
cn,
cr);
},
executeOptimizedSetter:function(cq,
bN,
bT,
cn,
cs){var bU=bN.$$properties[bT];
var bW=bN.prototype;
var cr=[];
var ct=cn===j||cn===m||cn===k||(cn===o&&bU.init===undefined);
var cu=cn===x||cn===u||cn===w;
var cv=bU.apply||bU.event||bU.inheritable;
if(cn===k||cn===w){var cj=this.$$store.runtime[bT];
}else if(cn===m||cn===u){var cj=this.$$store.theme[bT];
}else if(cn===o){var cj=this.$$store.init[bT];
}else{var cj=this.$$store.user[bT];
}{if(!bU.nullable||bU.check||bU.inheritable){cr.push(bs);
}if(cn===j){cr.push(bC,
bT,
bF,
cn,
bp);
}};
if(ct){if(bU.transform){cr.push(V,
bU.transform,
bh);
}}if(cv){if(ct){cr.push(f,
cj,
M);
}else if(cu){cr.push(f,
cj,
bc);
}}if(bU.inheritable){cr.push(bl);
}{};
if(!cv){if(cn===k){cr.push(e,
this.$$store.runtime[bT],
d);
}else if(cn===w){cr.push(f,
this.$$store.runtime[bT],
h);
cr.push(g,
this.$$store.runtime[bT],
b);
}else if(cn===j){cr.push(e,
this.$$store.user[bT],
d);
}else if(cn===x){cr.push(f,
this.$$store.user[bT],
h);
cr.push(g,
this.$$store.user[bT],
b);
}else if(cn===m){cr.push(e,
this.$$store.theme[bT],
d);
}else if(cn===u){cr.push(f,
this.$$store.theme[bT],
h);
cr.push(g,
this.$$store.theme[bT],
b);
}else if(cn===o&&ct){cr.push(e,
this.$$store.init[bT],
d);
}}else{if(bU.inheritable){cr.push(bg,
this.$$store.inherit[bT],
b);
}else{cr.push(bD);
}cr.push(f,
this.$$store.runtime[bT],
s);
if(cn===k){cr.push(c,
this.$$store.runtime[bT],
d);
}else if(cn===w){cr.push(g,
this.$$store.runtime[bT],
b);
cr.push(f,
this.$$store.user[bT],
h);
cr.push(c,
this.$$store.user[bT],
b);
cr.push(p,
this.$$store.theme[bT],
h);
cr.push(c,
this.$$store.theme[bT],
b);
cr.push(p,
this.$$store.init[bT],
s);
cr.push(c,
this.$$store.init[bT],
b);
cr.push(e,
this.$$store.useinit[bT],
v);
cr.push(n);
}else{cr.push(E,
this.$$store.runtime[bT],
b);
if(cn===j){cr.push(e,
this.$$store.user[bT],
d);
}else if(cn===x){cr.push(g,
this.$$store.user[bT],
b);
}else if(cn===m){cr.push(e,
this.$$store.theme[bT],
d);
}else if(cn===u){cr.push(g,
this.$$store.theme[bT],
b);
}else if(cn===o&&ct){cr.push(e,
this.$$store.init[bT],
d);
}}cr.push(n);
cr.push(p,
this.$$store.user[bT],
s);
if(cn===j){if(!bU.inheritable){cr.push(y,
this.$$store.user[bT],
b);
}cr.push(c,
this.$$store.user[bT],
d);
}else if(cn===x){if(!bU.inheritable){cr.push(y,
this.$$store.user[bT],
b);
}cr.push(g,
this.$$store.user[bT],
b);
cr.push(f,
this.$$store.runtime[bT],
h);
cr.push(c,
this.$$store.runtime[bT],
b);
cr.push(f,
this.$$store.theme[bT],
h);
cr.push(c,
this.$$store.theme[bT],
b);
cr.push(p,
this.$$store.init[bT],
s);
cr.push(c,
this.$$store.init[bT],
b);
cr.push(e,
this.$$store.useinit[bT],
v);
cr.push(n);
}else{if(cn===k){cr.push(c,
this.$$store.runtime[bT],
d);
}else if(bU.inheritable){cr.push(c,
this.$$store.user[bT],
b);
}else{cr.push(E,
this.$$store.user[bT],
b);
}if(cn===m){cr.push(e,
this.$$store.theme[bT],
d);
}else if(cn===u){cr.push(g,
this.$$store.theme[bT],
b);
}else if(cn===o&&ct){cr.push(e,
this.$$store.init[bT],
d);
}}cr.push(n);
if(bU.themeable){cr.push(p,
this.$$store.theme[bT],
s);
if(!bU.inheritable){cr.push(y,
this.$$store.theme[bT],
b);
}
if(cn===k){cr.push(c,
this.$$store.runtime[bT],
d);
}else if(cn===j){cr.push(c,
this.$$store.user[bT],
d);
}else if(cn===m){cr.push(c,
this.$$store.theme[bT],
d);
}else if(cn===u){cr.push(g,
this.$$store.theme[bT],
b);
cr.push(f,
this.$$store.init[bT],
s);
cr.push(c,
this.$$store.init[bT],
b);
cr.push(e,
this.$$store.useinit[bT],
v);
cr.push(n);
}else if(cn===o){if(ct){cr.push(e,
this.$$store.init[bT],
d);
}cr.push(c,
this.$$store.theme[bT],
b);
}else if(cn===z){cr.push(c,
this.$$store.theme[bT],
b);
}cr.push(n);
}cr.push(p,
this.$$store.useinit[bT],
bz);
if(!bU.inheritable){cr.push(y,
this.$$store.init[bT],
b);
}
if(cn===o){if(ct){cr.push(c,
this.$$store.init[bT],
d);
}else{cr.push(c,
this.$$store.init[bT],
b);
}}else if(cn===j||cn===k||cn===m||cn===z){cr.push(g,
this.$$store.useinit[bT],
b);
if(cn===k){cr.push(c,
this.$$store.runtime[bT],
d);
}else if(cn===j){cr.push(c,
this.$$store.user[bT],
d);
}else if(cn===m){cr.push(c,
this.$$store.theme[bT],
d);
}else if(cn===z){cr.push(c,
this.$$store.init[bT],
b);
}}cr.push(n);
if(cn===j||cn===k||cn===m||cn===o){cr.push(N);
if(cn===k){cr.push(c,
this.$$store.runtime[bT],
d);
}else if(cn===j){cr.push(c,
this.$$store.user[bT],
d);
}else if(cn===m){cr.push(c,
this.$$store.theme[bT],
d);
}else if(cn===o){if(ct){cr.push(c,
this.$$store.init[bT],
d);
}else{cr.push(c,
this.$$store.init[bT],
b);
}cr.push(e,
this.$$store.useinit[bT],
v);
}cr.push(n);
}}
if(bU.inheritable){cr.push(bE);
if(cn===z){cr.push(bq);
}else{cr.push(bx,
this.$$store.inherit[bT],
b);
}cr.push(X);
cr.push(e,
this.$$store.init[bT],
bA);
cr.push(e,
this.$$store.init[bT],
G);
cr.push(c,
this.$$store.init[bT],
b);
cr.push(e,
this.$$store.useinit[bT],
v);
cr.push(bH);
cr.push(g,
this.$$store.useinit[bT],
bb);
cr.push(n);
cr.push(D);
cr.push(bv);
cr.push(bu,
this.$$store.inherit[bT],
b);
cr.push(n);
cr.push(bB);
cr.push(g,
this.$$store.inherit[bT],
b);
cr.push(T,
this.$$store.inherit[bT],
bI);
cr.push(bG);
cr.push(A);
cr.push(br);
}else if(cv){if(cn!==j&&cn!==k&&cn!==m){cr.push(K);
}cr.push(D);
cr.push(A);
}if(cv){if(bU.apply){cr.push(e,
bU.apply,
bo,
bT,
bf);
}if(bU.event){cr.push(bn,
S,
bU.event,
R,
ba,
bU.event,
be,
H);
}if(bU.inheritable&&bW._getChildren){cr.push(W);
cr.push(bd,
this.$$method.refresh[bT],
bt,
this.$$method.refresh[bT],
bk);
cr.push(n);
}}if(ct){cr.push(L);
}return this.__R(cq,
bW,
bT,
cn,
cr,
cs);
}},
settings:{"qx.propertyDebugLevel":0}});
})();
(function(){var c="qx.core.ObjectRegistry";
qx.Bootstrap.define(c,
{statics:{__S:{},
__T:0,
inShutDown:false,
__U:[],
register:function(d){var e=this.__S;
if(!e){return;
}var f=d.$$hash;
if(f==null){var g=this.__U;
if(g.length>0){f=g.pop();
}else{f=(this.__T++).toString(36);
}d.$$hash=f;
}{};
e[f]=d;
},
unregister:function(d){var f=d.$$hash;
if(f==null){return;
}var e=this.__S;
if(e&&e[f]){delete e[f];
this.__U.push(f);
}},
toHashCode:function(d){{};
var f=d.$$hash;
if(f!=null){return f;
}var g=this.__U;
if(g.length>0){f=g.pop();
}else{f=(this.__T++).toString(36);
}return d.$$hash=f;
},
fromHashCode:function(f){return this.__S[f]||null;
},
shutdown:function(){this.inShutDown=true;
var e=this.__S;
var h=[];
for(var f in e){h.push(f);
}h.sort(function(j,
k){return parseInt(k,
36)-parseInt(j,
36);
});
var d,
m=0,
n=h.length;
while(true){try{for(;m<n;m++){f=h[m];
d=e[f];
if(d&&d.dispose){d.dispose();
}}}catch(ex){qx.log.Logger.error(this,
"Could not dispose object "+d.toString()+": "+ex);
if(m!==0){continue;
}}break;
}qx.log.Logger.debug(this,
"Disposed "+n+" objects");
delete this.__S;
},
getRegistry:function(){return this.__S;
}}});
})();
(function(){var a="unknown",
b="node",
c="error",
d="...(+",
e="array",
f=")",
g="info",
h="instance",
j="string",
k="null",
m="class",
n="number",
o="stringify",
p="]",
q="function",
r="boolean",
s="qx.deprecationWarnings",
t="map",
u="on",
v="undefined",
w="qx.log.Logger",
x=")}",
y="#",
z="warn",
A="document",
B="{...(",
C="[",
D="text[",
E="[...(",
F="\n",
G="debug",
H=")]",
I="object";
qx.Bootstrap.define(w,
{statics:{__V:50,
__W:"debug",
setLevel:function(J){this.__W=J;
},
getLevel:function(){return this.__W;
},
setTreshold:function(J){this.__V=J;
},
getTreshold:function(){return this.__V;
},
__X:{},
__Y:0,
register:function(K){if(K.$$id){return;
}var L=this.__Y++;
this.__X[L]=K;
K.$$id=L;
var M=this.__ba;
for(var N=0,
O=M.length;N<O;N++){K.process(M[N]);
}},
unregister:function(K){var L=K.$$id;
if(L==null){return;
}delete this.__X[L];
delete K.$$id;
},
debug:function(P,
Q){this.__bc(G,
arguments);
},
info:function(P,
Q){this.__bc(g,
arguments);
},
warn:function(P,
Q){this.__bc(z,
arguments);
},
error:function(P,
Q){this.__bc(c,
arguments);
},
trace:function(P){this.__bc(g,
[P,
qx.dev.StackTrace.getStackTrace().join(F)]);
},
deprecatedMethodWarning:function(R,
S){if(qx.core.Variant.isSet(s,
u)){var T=qx.lang.Function.getName(R);
var U=R.self?R.self.classname:a;
this.warn("The method '"+T+"' of class '"+U+"' is deprecated: "+S||"Please consult the API documentation of this method for alternatives.");
this.trace();
}},
deprecatedClassWarning:function(V,
S){if(qx.core.Variant.isSet(s,
u)){var U=V.self?V.self.classname:a;
this.warn("The method class '"+U+"' is deprecated: "+S||"Please consult the API documentation of this class for alternatives.");
this.trace();
}},
clear:function(){this.__ba=[];
},
__ba:[],
__bb:{debug:0,
info:1,
warn:2,
error:3},
__bc:function(W,
X){var Y=this.__bb;
if(Y[W]<Y[this.__W]){return;
}var P=X.length<2?null:X[0];
var ba=P?1:0;
var bb=[];
for(var N=ba,
O=X.length;N<O;N++){bb.push(this.__be(X[N],
true));
}var bc=new Date;
var bd={time:bc,
offset:bc-qx.Bootstrap.LOADSTART,
level:W,
items:bb};
if(P){if(P instanceof qx.core.Object){bd.object=P.$$hash;
}else if(P.$$type){bd.clazz=P;
}}var M=this.__ba;
M.push(bd);
if(M.length>(this.__V+10)){M.splice(this.__V,
M.length);
}var K=this.__X;
for(var L in K){K[L].process(bd);
}},
__bd:function(J){if(J===undefined){return v;
}else if(J===null){return k;
}
if(J.$$type){return m;
}var be=typeof J;
if(be===q||be==j||be===n||be===r){return be;
}else if(be===I){if(J.nodeType){return b;
}else if(J.classname){return h;
}else if(J instanceof Array){return e;
}else if(J instanceof Error){return c;
}else{return t;
}}
if(J.toString){return o;
}return a;
},
__be:function(J,
bf){var be=this.__bd(J);
var bg=a;
switch(be){case k:case v:bg=be;
break;
case j:case n:case r:bg=J;
break;
case b:if(J.nodeType===9){bg=A;
}else if(J.nodeType===3){bg=D+J.nodeValue+p;
}else if(J.nodeType===1){bg=J.nodeName.toLowerCase();
if(J.id){bg+=y+J.id;
}}else{bg=b;
}break;
case q:bg=qx.lang.Function.getName(J)||be;
break;
case h:bg=J.basename+C+J.$$hash+p;
break;
case m:case o:case c:bg=J.toString();
break;
case e:if(bf){bg=[];
for(var N=0,
O=J.length;N<O;N++){if(bg.length>20){bg.push(d+(O-N)+f);
break;
}bg.push(this.__be(J[N],
false));
}}else{bg=E+J.length+H;
}break;
case t:if(bf){var bh;
var bi=[];
for(var bj in J){bi.push(bj);
}bi.sort();
bg=[];
for(var N=0,
O=bi.length;N<O;N++){if(bg.length>20){bg.push(d+(O-N)+f);
break;
}bj=bi[N];
bh=this.__be(J[bj],
false);
bh.key=bj;
bg.push(bh);
}}else{var bk=0;
for(var bj in J){bk++;
}bg=B+bk+x;
}break;
}return {type:be,
text:bg};
}}});
})();
(function(){var a="qx.core.Object",
b="]",
c="__bg",
d="[",
f="string",
g="Object";
qx.Class.define(a,
{extend:Object,
construct:function(){qx.core.ObjectRegistry.register(this);
},
statics:{$$type:g},
members:{toHashCode:function(){return this.$$hash;
},
toString:function(){return this.classname+d+this.$$hash+b;
},
base:function(h,
j){if(arguments.length===1){return h.callee.base.call(this);
}else{return h.callee.base.apply(this,
Array.prototype.slice.call(arguments,
1));
}},
self:function(h){return h.callee.self;
},
clone:function(){var k=this.constructor;
var m=new k;
var n=qx.Class.getProperties(k);
var o=qx.core.Property.$$store.user;
var p=qx.core.Property.$$method.set;
var q;
for(var r=0,
s=n.length;r<s;r++){q=n[r];
if(this.hasOwnProperty(o[q])){m[p[q]](this[o[q]]);
}}return m;
},
serialize:function(){var k=this.constructor;
var n=qx.Class.getProperties(k);
var o=qx.core.Property.$$store.user;
var p=qx.core.Property.$$method.set;
var q;
var t={classname:k.classname,
properties:{}};
for(var r=0,
s=n.length;r<s;r++){q=n[r];
if(this.hasOwnProperty(o[q])){v=this[o[q]];
if(v instanceof qx.core.Object){t.properties[q]={$$hash:v.$$hash};
}else{t.properties[q]=v;
}}}return t;
},
set:function(u,
v){var p=qx.core.Property.$$method.set;
if(typeof u===f){{};
return this[p[u]](v);
}else{for(var w in u){{};
this[p[w]](u[w]);
}return this;
}},
get:function(w){var x=qx.core.Property.$$method.get;
{};
return this[x[w]]();
},
reset:function(w){var y=qx.core.Property.$$method.reset;
{};
this[y[w]]();
},
__bf:qx.event.Registration,
addListener:function(z,
A,
B,
C){if(!this.$$disposed){this.__bf.addListener(this,
z,
A,
B,
C);
}},
addListenerOnce:function(z,
A,
B,
C){var D=function(E){A.call(B||this,
E);
this.removeListener(z,
D,
this,
C);
};
this.addListener(z,
D,
this,
C);
},
removeListener:function(z,
A,
B,
C){if(!this.$$disposed){this.__bf.removeListener(this,
z,
A,
B,
C);
}},
hasListener:function(z,
C){return this.__bf.hasListener(this,
z,
C);
},
dispatchEvent:function(F){if(!this.$$disposed){return this.__bf.dispatchEvent(this,
F);
}return true;
},
fireEvent:function(z,
k,
h){if(!this.$$disposed){return this.__bf.fireEvent(this,
z,
k,
h);
}return true;
},
fireNonBubblingEvent:function(z,
k,
h){if(!this.$$disposed){return this.__bf.fireNonBubblingEvent(this,
z,
k,
h);
}return true;
},
fireDataEvent:function(z,
u,
G,
H){if(!this.$$disposed){return this.__bf.fireNonBubblingEvent(this,
z,
qx.event.type.Data,
[u,
G||null,
!!H]);
}return true;
},
__bg:null,
setUserData:function(I,
v){if(!this.__bg){this.__bg={};
}this.__bg[I]=v;
},
getUserData:function(I){if(!this.__bg){return null;
}return this.__bg[I];
},
__bh:qx.log.Logger,
debug:function(J){this.__bh.debug(this,
J);
},
info:function(J){this.__bh.info(this,
J);
},
warn:function(J){this.__bh.warn(this,
J);
},
error:function(J){this.__bh.error(this,
J);
},
trace:function(){this.__bh.trace(this);
},
isDisposed:function(){return this.$$disposed||false;
},
dispose:function(){if(this.$$disposed){return;
}this.$$disposed=true;
{};
var k=this.constructor;
var K;
while(k.superclass){if(k.$$destructor){k.$$destructor.call(this);
}if(k.$$includes){K=k.$$flatIncludes;
for(var r=0,
s=K.length;r<s;r++){if(K[r].$$destructor){K[r].$$destructor.call(this);
}}}k=k.superclass;
}var I,
v;
},
_disposeFields:function(L){qx.util.DisposeUtil.disposeFields(this,
arguments);
},
_disposeObjects:function(L){qx.util.DisposeUtil.disposeObjects(this,
arguments);
},
_disposeArray:function(M){qx.util.DisposeUtil.disposeArray(this,
M);
},
_disposeMap:function(M){qx.util.DisposeUtil.disposeMap(this,
M);
}},
settings:{"qx.disposerDebugLevel":0},
defer:function(N){{};
},
destruct:function(){qx.event.Registration.removeAllListeners(this);
qx.core.ObjectRegistry.unregister(this);
this._disposeFields(c);
var k=this.constructor;
var O;
var P=qx.core.Property.$$store;
var Q=P.user;
var R=P.theme;
var S=P.inherit;
var T=P.useinit;
var U=P.init;
while(k){O=k.$$properties;
if(O){for(var q in O){if(O[q].dispose){this[Q[q]]=this[R[q]]=this[S[q]]=this[T[q]]=this[U[q]]=undefined;
}}}k=k.superclass;
}}});
})();
(function(){var a="",
b="g",
c="0",
d='\\$1',
e="%",
f='-',
g="qx.lang.String",
h="undefined";
qx.Bootstrap.define(g,
{statics:{camelCase:function(j){return j.replace(/\-([a-z])/g,
function(k,
l){return l.toUpperCase();
});
},
hyphenate:function(j){return j.replace(/[A-Z]/g,
function(k){return (f+k.charAt(0).toLowerCase());
});
},
capitalize:function(j){return j.replace(/\b[a-z]/g,
function(k){return k.toUpperCase();
});
},
trimLeft:function(j){return j.replace(/^\s+/,
a);
},
trimRight:function(j){return j.replace(/\s+$/,
a);
},
trim:function(j){return j.replace(/^\s+|\s+$/g,
a);
},
startsWith:function(m,
n){return m.substring(0,
n.length)===n;
},
endsWith:function(m,
n){return m.substring(m.length-n.length,
m.length)===n;
},
pad:function(j,
o,
p){if(typeof p===h){p=c;
}var q=a;
for(var r=j.length;r<o;r++){q+=p;
}return q+j;
},
firstUp:function(j){return j.charAt(0).toUpperCase()+j.substr(1);
},
firstLow:function(j){return j.charAt(0).toLowerCase()+j.substr(1);
},
contains:function(j,
s){return j.indexOf(s)!=-1;
},
format:function(t,
u){var j=t;
for(var r=0;r<u.length;r++){j=j.replace(new RegExp(e+(r+1),
b),
u[r]);
}return j;
},
escapeRegexpChars:function(j){return j.replace(/([.*+?^${}()|[\]\/\\])/g,
d);
},
toArray:function(j){return j.split(/\B|\b/g);
},
stripTags:function(j){return j.replace(/<\/?[^>]+>/gi,
a);
}}});
})();
(function(){var a="function",
b="]",
c="Interface",
d="[Interface ",
e="qx.Interface";
qx.Class.define(e,
{statics:{define:function(f,
g){if(g){if(g.extend&&!(g.extend instanceof Array)){g.extend=[g.extend];
}{};
var h=g.statics?g.statics:{};
if(g.extend){h.$$extends=g.extend;
}
if(g.properties){h.$$properties=g.properties;
}
if(g.members){h.$$members=g.members;
}
if(g.events){h.$$events=g.events;
}}else{var h={};
}h.$$type=c;
h.name=f;
h.toString=this.genericToString;
h.basename=qx.Bootstrap.createNamespace(f,
h);
qx.Interface.$$registry[f]=h;
return h;
},
getByName:function(f){return this.$$registry[f];
},
isDefined:function(f){return this.getByName(f)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.$$registry);
},
flatten:function(j){if(!j){return [];
}var k=j.concat();
for(var m=0,
n=j.length;m<n;m++){if(j[m].$$extends){k.push.apply(k,
this.flatten(j[m].$$extends));
}}return k;
},
assert:function(o,
h,
p){var q=h.$$members;
if(q){var r=o.prototype;
for(var s in q){if(typeof q[s]===a){if(typeof r[s]===a){if(p===true&&!qx.Class.hasInterface(o,
h)){r[s]=this.__bi(h,
r[s],
s,
q[s]);
}}else{var t=s.match(/^(get|set|reset)(.*)$/);
if(!t||!qx.Class.hasProperty(o,
qx.lang.String.firstLow(t[2]))){throw new Error('Implementation of method "'+s+'" is missing in class "'+o.classname+'" required by interface "'+h.name+'"');
}}}else{if(typeof r[s]===undefined){if(typeof r[s]!==a){throw new Error('Implementation of member "'+s+'" is missing in class "'+o.classname+'" required by interface "'+h.name+'"');
}}}}}if(h.$$properties){for(var s in h.$$properties){if(!qx.Class.hasProperty(o,
s)){throw new Error('The property "'+s+'" is not supported by Class "'+o.classname+'"!');
}}}if(h.$$events){for(var s in h.$$events){if(!qx.Class.supportsEvent(o,
s)){throw new Error('The event "'+s+'" is not supported by Class "'+o.classname+'"!');
}}}var u=h.$$extends;
if(u){for(var m=0,
n=u.length;m<n;m++){this.assert(o,
u[m],
p);
}}},
genericToString:function(){return d+this.name+b;
},
$$registry:{},
__bi:function(){},
__bj:null,
__bk:function(){}}});
})();
(function(){var a="qx.event.IEventHandler";
qx.Interface.define(a,
{statics:{TARGET_DOMNODE:1,
TARGET_WINDOW:2,
TARGET_OBJECT:3},
members:{canHandleEvent:function(b,
c){},
registerEvent:function(b,
c,
d){},
unregisterEvent:function(b,
c,
d){}}});
})();
(function(){var a="load",
b="unload",
c="ready",
d="shutdown",
f="qx.event.handler.Application",
g="_window";
qx.Class.define(f,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(h){arguments.callee.base.call(this);
this._window=h.getWindow();
this._initObserver();
qx.event.handler.Application.$$instance=this;
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{ready:1,
shutdown:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_WINDOW,
IGNORE_CAN_HANDLE:true,
ready:function(){var i=qx.event.handler.Application.$$instance;
if(i){i.__bl();
}}},
members:{canHandleEvent:function(j,
k){},
registerEvent:function(j,
k,
l){},
unregisterEvent:function(j,
k,
l){},
__bl:function(){if(!this.__bm){this.__bm=true;
qx.event.Registration.fireEvent(window,
c);
}},
_initObserver:function(){this._onNativeLoadWrapped=qx.lang.Function.bind(this._onNativeLoad,
this);
this._onNativeUnloadWrapped=qx.lang.Function.bind(this._onNativeUnload,
this);
qx.bom.Event.addNativeListener(window,
a,
this._onNativeLoadWrapped);
qx.bom.Event.addNativeListener(window,
b,
this._onNativeUnloadWrapped);
},
_stopObserver:function(){qx.bom.Event.removeNativeListener(window,
a,
this._onNativeLoadWrapped);
qx.bom.Event.removeNativeListener(window,
b,
this._onNativeUnloadWrapped);
this._onNativeLoadWrapped=null;
this._onNativeUnloadWrapped=null;
},
_onNativeLoad:function(m){if(!window.qxloader){this.__bl();
}},
_onNativeUnload:function(m){if(!this.__bn){this.__bn=true;
qx.event.Registration.fireEvent(window,
d);
qx.core.ObjectRegistry.shutdown();
}}},
destruct:function(){this._stopObserver();
this._disposeFields(g);
},
defer:function(n){qx.event.Registration.addHandler(n);
}});
})();
(function(){var a="qx.event.IEventDispatcher";
qx.Interface.define(a,
{members:{canDispatchEvent:function(b,
c,
d){this.assertInstance(c,
qx.event.type.Event);
this.assertString(d);
},
dispatchEvent:function(b,
c,
d){this.assertInstance(c,
qx.event.type.Event);
this.assertString(d);
}}});
})();
(function(){var a="qx.event.dispatch.Direct";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventDispatcher,
construct:function(b){this._manager=b;
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_LAST},
members:{canDispatchEvent:function(c,
d,
e){return !d.getBubbles();
},
dispatchEvent:function(c,
d,
e){d.setEventPhase(qx.event.type.Event.AT_TARGET);
var f=this._manager.getListeners(c,
e,
false);
if(f){for(var g=0,
h=f.length;g<h;g++){var j=f[g].context||c;
f[g].handler.call(j,
d);
}}}},
defer:function(k){qx.event.Registration.addDispatcher(k);
}});
})();
(function(){var a="ready",
b="qx.application",
c="qx.core.Init",
d="shutdown";
qx.Class.define(c,
{statics:{getApplication:function(){return this.__bp||null;
},
__bo:function(){qx.log.Logger.debug(this,
"Load runtime: "+(new Date-qx.Bootstrap.LOADSTART)+"ms");
var e=qx.core.Setting.get(b);
var f=qx.Class.getByName(e);
if(f){this.__bp=new f;
var g=new Date;
this.__bp.main();
qx.log.Logger.debug(this,
"Main runtime: "+(new Date-g)+"ms");
var g=new Date;
this.__bp.finalize();
qx.log.Logger.debug(this,
"Finalize runtime: "+(new Date-g)+"ms");
}else{qx.log.Logger.warn("Missing application class: "+e);
}},
__bq:function(){var e=this.__bp;
if(e){e.terminate();
}}},
defer:function(h){qx.event.Registration.addListener(window,
a,
h.__bo,
h);
qx.event.Registration.addListener(window,
d,
h.__bq,
h);
}});
})();
(function(){var a="qx.application.IApplication";
qx.Interface.define(a,
{members:{main:function(){},
finalize:function(){},
terminate:function(){}}});
})();
(function(){var a="qx.Mixin",
b="]",
c="Mixin",
d="[Mixin ";
qx.Class.define(a,
{statics:{define:function(e,
f){if(f){if(f.include&&!(f.include instanceof Array)){f.include=[f.include];
}{};
var g=f.statics?f.statics:{};
for(var h in g){g[h].mixin=g;
}if(f.construct){g.$$constructor=f.construct;
}
if(f.include){g.$$includes=f.include;
}
if(f.properties){g.$$properties=f.properties;
}
if(f.members){g.$$members=f.members;
}
for(var h in g.$$members){if(g.$$members[h] instanceof Function){g.$$members[h].mixin=g;
}}
if(f.events){g.$$events=f.events;
}
if(f.destruct){g.$$destructor=f.destruct;
}}else{var g={};
}g.$$type=c;
g.name=e;
g.toString=this.genericToString;
g.basename=qx.Bootstrap.createNamespace(e,
g);
this.$$registry[e]=g;
return g;
},
checkCompatibility:function(j){var k=this.flatten(j);
var m=k.length;
if(m<2){return true;
}var n={};
var o={};
var p={};
var g;
for(var q=0;q<m;q++){g=k[q];
for(var h in g.events){if(p[h]){throw new Error('Conflict between mixin "'+g.name+'" and "'+p[h]+'" in member "'+h+'"!');
}p[h]=g.name;
}
for(var h in g.properties){if(n[h]){throw new Error('Conflict between mixin "'+g.name+'" and "'+n[h]+'" in property "'+h+'"!');
}n[h]=g.name;
}
for(var h in g.members){if(o[h]){throw new Error('Conflict between mixin "'+g.name+'" and "'+o[h]+'" in member "'+h+'"!');
}o[h]=g.name;
}}return true;
},
isCompatible:function(g,
r){var k=qx.Class.getMixins(r);
k.push(g);
return qx.Mixin.checkCompatibility(k);
},
getByName:function(e){return this.$$registry[e];
},
isDefined:function(e){return this.getByName(e)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.$$registry);
},
flatten:function(j){if(!j){return [];
}var k=j.concat();
for(var q=0,
s=j.length;q<s;q++){if(j[q].$$includes){k.push.apply(k,
this.flatten(j[q].$$includes));
}}return k;
},
genericToString:function(){return d+this.name+b;
},
$$registry:{},
__br:null,
__bs:function(){}}});
})();
(function(){var a="qx.locale.MTranslation";
qx.Mixin.define(a,
{members:{tr:function(b,
c){var d=qx.locale.Manager;
if(d){return d.tr.apply(d,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
},
trn:function(e,
f,
g,
c){var d=qx.locale.Manager;
if(d){return d.trn.apply(d,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
},
marktr:function(b){var d=qx.locale.Manager;
if(d){return d.marktr.apply(d,
arguments);
}throw new Error("To enable localization please include qx.locale.Manager into your build!");
}}});
})();
(function(){var a="abstract",
b="__bt",
c="qx.application.AbstractGui";
qx.Class.define(c,
{type:a,
extend:qx.core.Object,
implement:[qx.application.IApplication],
include:qx.locale.MTranslation,
members:{__bt:null,
_createRootWidget:function(){throw new Error("Abstract method call");
},
getRoot:function(){return this.__bt;
},
main:function(){qx.theme.manager.Meta.getInstance().initialize();
this.__bt=this._createRootWidget();
},
finalize:function(){this.render();
},
render:function(){qx.ui.core.queue.Manager.flush();
},
terminate:function(){}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="qx.application.Standalone";
qx.Class.define(a,
{extend:qx.application.AbstractGui,
members:{_createRootWidget:function(){return new qx.ui.root.Application(document);
}}});
})();
(function(){var a="opentest",
b="changeSelection",
c="list",
d="application/xml",
f="deletetest",
g="Test Case",
h="Test Cases",
j="GET",
k="details",
l="dblclick",
m="org.argeo.slc.web.Application",
n="applet",
o="stop",
p="//data",
q="vertical",
r="icon",
s="60%",
t="Details",
u="#fff",
v="completed",
w="horizontal",
x="Date",
y="Test",
z="copytocollection";
qx.Class.define(m,
{extend:qx.application.Standalone,
properties:{model:{nullable:true},
commandManager:{}},
members:{main:function(){arguments.callee.base.call(this);
this.views={};
{};
var A=new qx.ui.layout.VBox();
var B=new qx.ui.container.Composite(A);
var C=new qx.ui.menubar.MenuBar();
var D=new qx.ui.toolbar.ToolBar();
this.commandManager=new org.argeo.slc.web.event.CommandsManager(this);
this.commandManager.createCommands();
this.commandManager.createMenuButtons(C);
this.commandManager.createToolbarParts(D);
D.setShow(r);
this.commandManager.addToolbarContextMenu(D);
var E=this.commandManager.getCommandById(o);
var F=org.argeo.slc.web.util.RequestManager.getInstance();
F.setStopCommand(E);
var G=new qx.ui.splitpane.Pane(w);
var H=new qx.ui.splitpane.Pane(q).set({width:300,
minWidth:100});
H.setDecorator(null);
var I=new org.argeo.slc.web.components.View(c,
h);
this.registerView(I);
var J=new org.argeo.slc.web.components.View(k,
t);
J.set({minHeight:200});
this.registerView(J);
H.add(I,
1);
H.add(J,
0);
G.add(H,
0);
this.rightPane=new org.argeo.slc.web.components.View(n,
y);
this.registerView(this.rightPane);
G.add(this.rightPane,
1);
B.add(C);
B.add(D);
B.add(G,
{flex:1});
var K=this.getRoot();
K.add(B,
{left:0,
right:0,
top:0,
bottom:0});
this.initializeViews();
},
registerView:function(L){this.views[L.getViewId()]=L;
L.getViewSelection().addListener(b,
function(M){this.commandManager.refreshCommands(M.getData());
},
this);
},
getSelectionForView:function(N){if(this.views[N]){return this.views[N].getViewSelection();
}this.error("Cannot find view '"+N+"'");
return null;
},
initializeViews:function(){var L=this.views[c];
var O=new qx.ui.table.model.Simple();
O.setColumns([g,
x]);
this.setModel(O);
this.table=new qx.ui.table.Table(O,
{tableColumnModel:function(P){return new qx.ui.table.columnmodel.Resize(P);
}});
this.table.set({statusBarVisible:false,
showCellFocusIndicator:false,
columnVisibilityButtonVisible:false,
contextMenu:this.commandManager.createMenuFromIds([a,
f,
z]),
decorator:new qx.ui.decoration.Background(u)});
this.table.addListener(l,
function(M){this.commandManager.executeCommand(a);
},
this);
var Q=this.table.getTableColumnModel();
Q.getBehavior().setWidth(0,
s);
Q.setDataCellRenderer(0,
new org.argeo.slc.web.components.XmlRenderer());
Q.setDataCellRenderer(1,
new org.argeo.slc.web.components.XmlRenderer());
this.table.getSelectionManager().getSelectionModel().addListener(b,
function(M){var R=L.getViewSelection();
R.clear();
var S=this.table.getSelectionManager().getSelectionModel();
if(!S.getSelectedCount()){return;
}var T=this.table.getSelectionManager().getSelectionModel().getSelectedRanges();
var U=this.getModel().getRowData(T[0].minIndex);
R.addNode(U);
L.setViewSelection(R);
},
this);
L.setContent(this.table,
false);
},
loadTable:function(V){var O=this.getModel();
O.removeRows(0,
O.getRowCount());
var F=org.argeo.slc.web.util.RequestManager.getInstance();
var W=F.getRequest(V,
j,
d);
W.addListener(v,
function(X){xml=X.getContent();
qx.log.Logger.info("Successfully loaded XML");
var Y=qx.xml.Element.selectNodes(xml,
p);
for(var ba=0;ba<Y.length;ba++){var bb=Y[ba];
O.addRows([bb]);
}F.requestCompleted(this);
},
W);
W.send();
},
createTestApplet:function(U){var bc=new org.argeo.slc.web.components.Applet();
bc.initData(U);
this.rightPane.empty();
this.rightPane.setContent(bc,
false);
}}});
})();
(function(){var a='"',
b="qx.lang.Core",
c="\\\\",
d="\\\"",
e="[object Error]";
qx.Bootstrap.define(b);
if(!Error.prototype.toString||Error.prototype.toString()==e){Error.prototype.toString=function(){return this.message;
};
}if(!Array.prototype.indexOf){Array.prototype.indexOf=function(f,
g){if(g==null){g=0;
}else if(g<0){g=Math.max(0,
this.length+g);
}
for(var h=g;h<this.length;h++){if(this[h]===f){return h;
}}return -1;
};
}
if(!Array.prototype.lastIndexOf){Array.prototype.lastIndexOf=function(f,
g){if(g==null){g=this.length-1;
}else if(g<0){g=Math.max(0,
this.length+g);
}
for(var h=g;h>=0;h--){if(this[h]===f){return h;
}}return -1;
};
}
if(!Array.prototype.forEach){Array.prototype.forEach=function(j,
k){var m=this.length;
for(var h=0;h<m;h++){j.call(k,
this[h],
h,
this);
}};
}
if(!Array.prototype.filter){Array.prototype.filter=function(j,
k){var m=this.length;
var n=[];
for(var h=0;h<m;h++){if(j.call(k,
this[h],
h,
this)){n.push(this[h]);
}}return n;
};
}
if(!Array.prototype.map){Array.prototype.map=function(j,
k){var m=this.length;
var n=[];
for(var h=0;h<m;h++){n.push(j.call(k,
this[h],
h,
this));
}return n;
};
}
if(!Array.prototype.some){Array.prototype.some=function(j,
k){var m=this.length;
for(var h=0;h<m;h++){if(j.call(k,
this[h],
h,
this)){return true;
}}return false;
};
}
if(!Array.prototype.every){Array.prototype.every=function(j,
k){var m=this.length;
for(var h=0;h<m;h++){if(!j.call(k,
this[h],
h,
this)){return false;
}}return true;
};
}if(!String.prototype.quote){String.prototype.quote=function(){return a+this.replace(/\\/g,
c).replace(/\"/g,
d)+a;
};
}})();
(function(){var a="indexOf",
b="lastIndexOf",
c="slice",
d="concat",
e="join",
f="toLocaleUpperCase",
g="shift",
h="substr",
j="filter",
k="unshift",
m="match",
n="quote",
o="qx.lang.Generics",
p="localeCompare",
q="sort",
r="some",
t="charAt",
u="split",
v="substring",
w="pop",
x="toUpperCase",
y="replace",
z="push",
A="charCodeAt",
B="every",
C="reverse",
D="search",
E="forEach",
F="map",
G="toLowerCase",
H="splice",
I="toLocaleLowerCase";
qx.Bootstrap.define(o,
{statics:{__bu:{"Array":[e,
C,
q,
z,
w,
g,
k,
H,
d,
c,
a,
b,
E,
F,
j,
r,
B],
"String":[n,
v,
G,
x,
t,
A,
a,
b,
I,
f,
p,
m,
D,
y,
u,
h,
d,
c]},
__bv:function(J,
K){return function(L){return J.prototype[K].apply(L,
Array.prototype.slice.call(arguments,
1));
};
},
__bw:function(){var M=qx.lang.Generics.__bu;
for(var N in M){var J=window[N];
var O=M[N];
for(var P=0,
Q=O.length;P<Q;P++){var K=O[P];
if(!J[K]){J[K]=qx.lang.Generics.__bv(J,
K);
}}}}},
defer:function(R){R.__bw();
}});
})();
(function(){var a=":",
b="qx.client",
c="anonymous",
d="...",
e="qx.dev.StackTrace",
f="",
g="\n",
h="/source/class/",
j=".";
qx.Class.define(e,
{statics:{getStackTrace:qx.core.Variant.select(b,
{"gecko":function(){try{throw new Error();
}catch(e){var k=this.getStackTraceFromError(e);
qx.lang.Array.removeAt(k,
0);
var l=this.getStackTraceFromCaller(arguments);
var m=l.length>k.length?l:k;
for(var n=0;n<Math.min(l.length,
k.length);n++){var o=l[n];
if(o.indexOf(c)>=0){continue;
}var p=o.split(a);
if(p.length!=2){continue;
}var q=p[0];
var r=p[1];
var s=k[n];
var t=s.split(a);
var u=t[0];
var v=t[1];
if(qx.Class.getByName(u)){var w=u;
}else{w=q;
}var x=w+a;
if(r){x+=r+a;
}x+=v;
m[n]=x;
}return m;
}},
"mshtml|webkit":function(){return this.getStackTraceFromCaller(arguments);
},
"opera":function(){var y;
try{y.bar();
}catch(e){var m=this.getStackTraceFromError(e);
qx.lang.Array.removeAt(m,
0);
return m;
}return [];
}}),
getStackTraceFromCaller:qx.core.Variant.select(b,
{"opera":function(z){return [];
},
"default":function(z){var m=[];
var A=qx.lang.Function.getCaller(z);
var B={};
while(A){var C=qx.lang.Function.getName(A);
m.push(C);
try{A=A.caller;
}catch(e){break;
}
if(!A){break;
}var D=qx.core.ObjectRegistry.toHashCode(A);
if(B[D]){m.push(d);
break;
}B[D]=A;
}return m;
}}),
getStackTraceFromError:qx.core.Variant.select(b,
{"gecko":function(E){if(!E.stack){return [];
}var F=/@(.+):(\d+)$/gm;
var G;
var m=[];
while((G=F.exec(E.stack))!=null){var H=G[1];
var v=G[2];
var w=this.__bx(H);
m.push(w+a+v);
}return m;
},
"webkit":function(E){if(E.sourceURL&&E.line){return [this.__bx(E.sourceURL)+a+E.line];
}else{return [];
}},
"opera":function(E){if(E.message.indexOf("Backtrace:")<0){return [];
}var m=[];
var I=qx.lang.String.trim(E.message.split("Backtrace:")[1]);
var J=I.split(g);
for(var n=0;n<J.length;n++){var K=J[n].match(/\s*Line ([0-9]+) of.* (\S.*)/);
if(K&&K.length>=2){var v=K[1];
var L=this.__bx(K[2]);
m.push(L+a+v);
}}return m;
},
"default":function(){return [];
}}),
__bx:function(L){var M=h;
var N=L.indexOf(M);
var w=(N==-1)?L:L.substring(N+M.length).replace(/\//g,
j).replace(/\.js$/,
f);
return w;
}}});
})();
(function(){var a="qx.util.ObjectPool",
b="Integer";
qx.Class.define(a,
{extend:qx.core.Object,
construct:function(c){arguments.callee.base.call(this);
this.__by={};
if(c!==undefined){this.setSize(c);
}},
properties:{size:{check:b,
init:null,
nullable:true}},
members:{getObject:function(d){if(this.$$disposed){return;
}
if(!d){throw new Error("Class needs to be defined!");
}var e=null;
var f=this.__by[d.classname];
if(f){e=f.pop();
}
if(e){e.$$pooled=false;
}else{e=new d;
}return e;
},
poolObject:function(e){if(!this.__by){return;
}var g=e.classname;
var f=this.__by[g];
if(e.$$pooled){throw new Error("Object is already pooled: "+e);
}
if(!f){this.__by[g]=f=[];
}var c=this.getSize()||Infinity;
if(f.length>c){this.warn("Cannot pool "+e+" because the pool is already full.");
e.dispose();
return;
}e.$$pooled=true;
f.push(e);
}},
destruct:function(){var f=this.__by;
var g,
h,
j,
k;
for(g in f){h=f[g];
for(j=0,
k=h.length;j<k;j++){h[j].dispose();
}}delete this.__by;
}});
})();
(function(){var a="singleton",
b="qx.event.Pool";
qx.Class.define(b,
{extend:qx.util.ObjectPool,
type:a,
construct:function(){arguments.callee.base.call(this,
30);
},
members:{__bz:{"qx.legacy.event.type.DragEvent":1,
"qx.legacy.event.type.MouseEvent":1,
"qx.legacy.event.type.KeyEvent":1},
poolObject:function(c){if(this.__bz[c.classname]){return;
}arguments.callee.base.call(this,
c);
}}});
})();
(function(){var a="_originalTarget",
b="_relatedTarget",
c="qx.event.type.Event",
d="_target",
e="_currentTarget";
qx.Class.define(c,
{extend:qx.core.Object,
statics:{CAPTURING_PHASE:1,
AT_TARGET:2,
BUBBLING_PHASE:3},
members:{init:function(f,
g){{};
this._type=null;
this._target=null;
this._currentTarget=null;
this._relatedTarget=null;
this._originalTarget=null;
this._stopPropagation=false;
this._preventDefault=false;
this._bubbles=!!f;
this._cancelable=!!g;
this._timeStamp=(new Date()).getTime();
this._eventPhase=null;
return this;
},
clone:function(h){if(h){var i=h;
}else{var i=qx.event.Pool.getInstance().getObject(this.constructor);
}i._type=this._type;
i._target=this._target;
i._currentTarget=this._currentTarget;
i._relatedTarget=this._relatedTarget;
i._originalTarget=this._originalTarget;
i._stopPropagation=this._stopPropagation;
i._bubbles=this._bubbles;
i._preventDefault=this._preventDefault;
i._cancelable=this._cancelable;
return i;
},
stopPropagation:function(){{};
this._stopPropagation=true;
},
getPropagationStopped:function(){return !!this._stopPropagation;
},
preventDefault:function(){{};
this._preventDefault=true;
},
getDefaultPrevented:function(){return !!this._preventDefault;
},
getType:function(){return this._type;
},
setType:function(j){this._type=j;
},
getEventPhase:function(){return this._eventPhase;
},
setEventPhase:function(k){this._eventPhase=k;
},
getTimeStamp:function(){return this._timeStamp;
},
getTarget:function(){return this._target;
},
setTarget:function(l){this._target=l;
},
getCurrentTarget:function(){return this._currentTarget||this._target;
},
setCurrentTarget:function(m){this._currentTarget=m;
},
getRelatedTarget:function(){return this._relatedTarget;
},
setRelatedTarget:function(n){this._relatedTarget=n;
},
getOriginalTarget:function(){return this._originalTarget;
},
setOriginalTarget:function(o){this._originalTarget=o;
},
getBubbles:function(){return this._bubbles;
},
setBubbles:function(p){this._bubbles=p;
},
isCancelable:function(){return this._cancelable;
},
setCancelable:function(g){this._cancelable=g;
}},
destruct:function(){this._disposeFields(d,
e,
b,
a);
}});
})();
(function(){var a="Better use 'getData'",
b="__bB",
c="Better use 'getOldData'",
d="__bA",
e="qx.event.type.Data";
qx.Class.define(e,
{extend:qx.event.type.Event,
members:{init:function(f,
g,
h){arguments.callee.base.call(this,
false,
h);
this.__bA=f;
this.__bB=g;
return this;
},
clone:function(i){var j=arguments.callee.base.call(this,
i);
j.__bA=this.__bA;
j.__bB=this.__bB;
return j;
},
getData:function(){return this.__bA;
},
getOldData:function(){return this.__bB;
},
getValue:function(){qx.log.Logger.deprecatedMethodWarning(arguments.callee,
a);
return this.__bA;
},
getOldValue:function(){qx.log.Logger.deprecatedMethodWarning(arguments.callee,
c);
return this.__bB;
}},
destruct:function(){this._disposeFields(d,
b);
}});
})();
(function(){var a="qx.event.handler.Object";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
statics:{PRIORITY:qx.event.Registration.PRIORITY_LAST,
SUPPORTED_TYPES:null,
TARGET_CHECK:qx.event.IEventHandler.TARGET_OBJECT,
IGNORE_CAN_HANDLE:false},
members:{canHandleEvent:function(b,
c){return qx.Class.supportsEvent(b.constructor,
c);
},
registerEvent:function(b,
c,
d){},
unregisterEvent:function(b,
c,
d){}},
defer:function(e){qx.event.Registration.addHandler(e);
}});
})();
(function(){var a="qx.util.DisposeUtil";
qx.Class.define(a,
{statics:{disposeFields:function(b,
c){var d;
for(var e=0,
f=c.length;e<f;e++){var d=c[e];
if(b[d]==null||!b.hasOwnProperty(d)){continue;
}b[d]=null;
}},
disposeObjects:function(b,
c){var d;
for(var e=0,
f=c.length;e<f;e++){d=c[e];
if(b[d]==null||!b.hasOwnProperty(d)){continue;
}
if(!qx.core.ObjectRegistry.inShutDown){if(b[d].dispose){b[d].dispose();
}else{throw new Error("Has no disposable object under key: "+d+"!");
}}b[d]=null;
}},
disposeArray:function(b,
g){var h=b[g];
if(!h){return;
}if(qx.core.ObjectRegistry.inShutDown){b[g]=null;
return;
}try{var j;
for(var e=h.length-1;e>=0;e--){j=h[e];
if(j){j.dispose();
}}}catch(ex){throw new Error("The array field: "+g+" of object: "+b+" has non disposable entries: "+ex);
}h.length=0;
b[g]=null;
},
disposeMap:function(b,
g){var h=b[g];
if(!h){return;
}if(qx.core.ObjectRegistry.inShutDown){b[g]=null;
return;
}try{for(var k in h){if(h.hasOwnProperty(k)){h[k].dispose();
}}}catch(ex){throw new Error("The map field: "+g+" of object: "+b+" has non disposable entries: "+ex);
}b[g]=null;
}}});
})();
(function(){var a="_applyTheme",
b="qx.theme",
c="qx.theme.manager.Meta",
d="qx.theme.Classic",
e="Theme",
f="singleton";
qx.Class.define(c,
{type:f,
extend:qx.core.Object,
properties:{theme:{check:e,
nullable:true,
apply:a}},
members:{_applyTheme:function(g,
h){var i=null;
var j=null;
var k=null;
var l=null;
var m=null;
if(g){i=g.meta.color||null;
j=g.meta.decoration||null;
k=g.meta.font||null;
l=g.meta.icon||null;
m=g.meta.appearance||null;
}var n=qx.theme.manager.Color.getInstance();
var o=qx.theme.manager.Decoration.getInstance();
var p=qx.theme.manager.Font.getInstance();
var q=qx.theme.manager.Icon.getInstance();
var r=qx.theme.manager.Appearance.getInstance();
n.setTheme(i);
o.setTheme(j);
p.setTheme(k);
q.setTheme(l);
r.setAppearanceTheme(m);
},
initialize:function(){var s=qx.core.Setting;
var t,
u;
t=s.get(b);
if(t){u=qx.Theme.getByName(t);
if(!u){throw new Error("The theme to use is not available: "+t);
}this.setTheme(u);
}}},
settings:{"qx.theme":d}});
})();
(function(){var a="_dynamic",
b="qx.util.ValueManager",
c="abstract";
qx.Class.define(b,
{type:c,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this._dynamic={};
},
members:{resolveDynamic:function(d){return this._dynamic[d];
},
isDynamic:function(d){return !!this._dynamic[d];
},
resolve:function(d){if(d&&this._dynamic[d]){return this._dynamic[d];
}return d;
}},
destruct:function(){this._disposeFields(a);
}});
})();
(function(){var a="_applyTheme",
b="qx.theme.manager.Color",
c="Theme",
d="changeTheme",
e="string",
f="singleton";
qx.Class.define(b,
{type:f,
extend:qx.util.ValueManager,
properties:{theme:{check:c,
nullable:true,
apply:a,
event:d}},
members:{_applyTheme:function(g){var h=this._dynamic={};
if(g){var i=g.colors;
var j=qx.util.ColorUtil;
var k;
for(var l in i){k=i[l];
if(typeof k===e){if(!j.isCssString(k)){throw new Error("Could not parse color: "+k);
}}else if(k instanceof Array){k=j.rgbToRgbString(k);
}else{throw new Error("Could not parse color: "+k);
}h[l]=k;
}}}}});
})();
(function(){var a=",",
c="rgb(",
d=")",
e="qx.theme.manager.Color",
h="qx.util.ColorUtil";
qx.Class.define(h,
{statics:{REGEXP:{hex3:/^#([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
hex6:/^#([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})([0-9a-fA-F]{1})$/,
rgb:/^rgb\(\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*,\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*,\s*([0-9]{1,3}\.{0,1}[0-9]*)\s*\)$/},
SYSTEM:{activeborder:true,
activecaption:true,
appworkspace:true,
background:true,
buttonface:true,
buttonhighlight:true,
buttonshadow:true,
buttontext:true,
captiontext:true,
graytext:true,
highlight:true,
highlighttext:true,
inactiveborder:true,
inactivecaption:true,
inactivecaptiontext:true,
infobackground:true,
infotext:true,
menu:true,
menutext:true,
scrollbar:true,
threeddarkshadow:true,
threedface:true,
threedhighlight:true,
threedlightshadow:true,
threedshadow:true,
window:true,
windowframe:true,
windowtext:true},
NAMED:{black:[0,
0,
0],
silver:[192,
192,
192],
gray:[128,
128,
128],
white:[255,
255,
255],
maroon:[128,
0,
0],
red:[255,
0,
0],
purple:[128,
0,
128],
fuchsia:[255,
0,
255],
green:[0,
128,
0],
lime:[0,
255,
0],
olive:[128,
128,
0],
yellow:[255,
255,
0],
navy:[0,
0,
128],
blue:[0,
0,
255],
teal:[0,
128,
128],
aqua:[0,
255,
255],
transparent:[-1,
-1,
-1],
magenta:[255,
0,
255],
orange:[255,
165,
0],
brown:[165,
42,
42]},
isNamedColor:function(j){return this.NAMED[j]!==undefined;
},
isSystemColor:function(j){return this.SYSTEM[j]!==undefined;
},
supportsThemes:function(){return qx.Class.isDefined(e);
},
isThemedColor:function(j){if(!this.supportsThemes()){return false;
}return qx.theme.manager.Color.getInstance().isDynamic(j);
},
stringToRgb:function(k){if(this.supportsThemes()&&this.isThemedColor(k)){var k=qx.theme.manager.Color.getInstance().resolveDynamic(k);
}
if(this.isNamedColor(k)){return this.NAMED[k];
}else if(this.isSystemColor(k)){throw new Error("Could not convert system colors to RGB: "+k);
}else if(this.isRgbString(k)){return this.__bC();
}else if(this.isHex3String(k)){return this.__bD();
}else if(this.isHex6String(k)){return this.__bE();
}throw new Error("Could not parse color: "+k);
},
cssStringToRgb:function(k){if(this.isNamedColor(k)){return this.NAMED[k];
}else if(this.isSystemColor(k)){throw new Error("Could not convert system colors to RGB: "+k);
}else if(this.isRgbString(k)){return this.__bC();
}else if(this.isHex3String(k)){return this.__bD();
}else if(this.isHex6String(k)){return this.__bE();
}throw new Error("Could not parse color: "+k);
},
stringToRgbString:function(k){return this.rgbToRgbString(this.stringToRgb(k));
},
rgbToRgbString:function(l){return c+l[0]+a+l[1]+a+l[2]+d;
},
rgbToHexString:function(l){return (qx.lang.String.pad(l[0].toString(16).toUpperCase(),
2)+qx.lang.String.pad(l[1].toString(16).toUpperCase(),
2)+qx.lang.String.pad(l[2].toString(16).toUpperCase(),
2));
},
isValidPropertyValue:function(k){return this.isThemedColor(k)||this.isNamedColor(k)||this.isHex3String(k)||this.isHex6String(k)||this.isRgbString(k);
},
isCssString:function(k){return this.isSystemColor(k)||this.isNamedColor(k)||this.isHex3String(k)||this.isHex6String(k)||this.isRgbString(k);
},
isHex3String:function(k){return this.REGEXP.hex3.test(k);
},
isHex6String:function(k){return this.REGEXP.hex6.test(k);
},
isRgbString:function(k){return this.REGEXP.rgb.test(k);
},
__bC:function(){var m=parseInt(RegExp.$1,
10);
var n=parseInt(RegExp.$2,
10);
var o=parseInt(RegExp.$3,
10);
return [m,
n,
o];
},
__bD:function(){var m=parseInt(RegExp.$1,
16)*17;
var n=parseInt(RegExp.$2,
16)*17;
var o=parseInt(RegExp.$3,
16)*17;
return [m,
n,
o];
},
__bE:function(){var m=(parseInt(RegExp.$1,
16)*16)+parseInt(RegExp.$2,
16);
var n=(parseInt(RegExp.$3,
16)*16)+parseInt(RegExp.$4,
16);
var o=(parseInt(RegExp.$5,
16)*16)+parseInt(RegExp.$6,
16);
return [m,
n,
o];
},
hex3StringToRgb:function(j){if(this.isHex3String(j)){return this.__bD(j);
}throw new Error("Invalid hex3 value: "+j);
},
hex6StringToRgb:function(j){if(this.isHex6String(j)){return this.__bE(j);
}throw new Error("Invalid hex6 value: "+j);
},
hexStringToRgb:function(j){if(this.isHex3String(j)){return this.__bD(j);
}
if(this.isHex6String(j)){return this.__bE(j);
}throw new Error("Invalid hex value: "+j);
},
rgbToHsb:function(l){var s,
u,
v;
var m=l[0];
var n=l[1];
var o=l[2];
var w=(m>n)?m:n;
if(o>w){w=o;
}var x=(m<n)?m:n;
if(o<x){x=o;
}v=w/255.0;
if(w!=0){u=(w-x)/w;
}else{u=0;
}
if(u==0){s=0;
}else{var y=(w-m)/(w-x);
var z=(w-n)/(w-x);
var A=(w-o)/(w-x);
if(m==w){s=A-z;
}else if(n==w){s=2.0+y-A;
}else{s=4.0+z-y;
}s=s/6.0;
if(s<0){s=s+1.0;
}}return [Math.round(s*360),
Math.round(u*100),
Math.round(v*100)];
},
hsbToRgb:function(B){var C,
D,
E,
F,
G;
var s=B[0]/360;
var u=B[1]/100;
var v=B[2]/100;
if(s>=1.0){s%=1.0;
}
if(u>1.0){u=1.0;
}
if(v>1.0){v=1.0;
}var H=Math.floor(255*v);
var l={};
if(u==0.0){l.red=l.green=l.blue=H;
}else{s*=6.0;
C=Math.floor(s);
D=s-C;
E=Math.floor(H*(1.0-u));
F=Math.floor(H*(1.0-(u*D)));
G=Math.floor(H*(1.0-(u*(1.0-D))));
switch(C){case 0:l.red=H;
l.green=G;
l.blue=E;
break;
case 1:l.red=F;
l.green=H;
l.blue=E;
break;
case 2:l.red=E;
l.green=H;
l.blue=G;
break;
case 3:l.red=E;
l.green=F;
l.blue=H;
break;
case 4:l.red=G;
l.green=E;
l.blue=H;
break;
case 5:l.red=H;
l.green=E;
l.blue=F;
break;
}}return l;
},
randomColor:function(){var I=Math.round(Math.random()*255);
var J=Math.round(Math.random()*255);
var K=Math.round(Math.random()*255);
return this.rgbToRgbString([I,
J,
K]);
}}});
})();
(function(){var a="decoration",
b="object",
c="_applyTheme",
d="__bF",
e="qx.theme.manager.Decoration",
f="Theme",
g="string",
h="singleton";
qx.Class.define(e,
{type:h,
extend:qx.core.Object,
properties:{theme:{check:f,
nullable:true,
apply:c}},
members:{resolve:function(i){if(!i){return null;
}
if(typeof i===b){return i;
}var j=this.getTheme();
if(!j){return null;
}var j=this.getTheme();
if(!j){return null;
}var k=this.__bF;
if(!k){k=this.__bF={};
}var l=k[i];
if(l){return l;
}var m=j.decorations[i];
if(!m){return null;
}var n=m.decorator;
if(n==null){throw new Error("Missing definition of which decorator to use in entry: "+i+"!");
}return k[i]=(new n).set(m.style);
},
isValidPropertyValue:function(i){if(typeof i===g){return this.isDynamic(i);
}else if(typeof i===b){var n=i.constructor;
return qx.Class.hasInterface(n,
qx.ui.decoration.IDecorator);
}return false;
},
isDynamic:function(i){if(!i){return false;
}var j=this.getTheme();
if(!j){return false;
}return !!j.decorations[i];
},
_applyTheme:function(i){var o=qx.util.AliasManager.getInstance();
i?o.add(a,
i.resource):o.remove(a);
}},
destruct:function(){this._disposeMap(d);
}});
})();
(function(){var a="qx.ui.decoration.IDecorator";
qx.Interface.define(a,
{members:{init:function(b){},
resize:function(b,
c,
d){},
tint:function(b,
e){},
getMarkup:function(){},
getInsets:function(){}}});
})();
(function(){var a="/",
b="_aliases",
c="0",
d="qx/static",
e="http://",
f="https://",
g="file://",
h="qx.util.AliasManager",
i="singleton",
j=".",
k="static";
qx.Class.define(h,
{type:i,
extend:qx.util.ValueManager,
construct:function(){arguments.callee.base.call(this);
this._aliases={};
this.add(k,
d);
},
members:{_preprocess:function(l){var m=this._dynamic;
if(m[l]===false){return l;
}else if(m[l]===undefined){if(l.charAt(0)===a||l.charAt(0)===j||l.indexOf(e)===0||l.indexOf(f)===c||l.indexOf(g)===0){m[l]=false;
return l;
}var n=l.substring(0,
l.indexOf(a));
var o=this._aliases[n];
if(o!==undefined){m[l]=o+l.substring(n.length);
}}return l;
},
add:function(n,
p){this._aliases[n]=p;
var m=this._dynamic;
var q={};
for(var r in m){if(r.substring(0,
r.indexOf(a))===n){m[r]=p+r.substring(n.length);
q[r]=true;
}}},
remove:function(n){delete this._aliases[n];
},
resolve:function(r){if(r!==null){r=this._preprocess(r);
}return this._dynamic[r]||r;
}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="qx.theme.manager.Font",
b="Theme",
c="changeTheme",
d="_applyTheme",
e="singleton";
qx.Class.define(a,
{type:e,
extend:qx.util.ValueManager,
properties:{theme:{check:b,
nullable:true,
apply:d,
event:c}},
members:{resolveDynamic:function(f){return f instanceof qx.bom.Font?f:this._dynamic[f];
},
isDynamic:function(f){return f&&(f instanceof qx.bom.Font||this._dynamic[f]!==undefined);
},
_applyTheme:function(f){var g=this._dynamic;
for(var h in g){if(g[h].themed){g[h].dispose();
delete g[h];
}}
if(f){var i=f.fonts;
var j=qx.bom.Font;
for(var h in i){g[h]=(new j).set(i[h]);
g[h].themed=true;
}}}}});
})();
(function(){var a="",
b="underline",
c="Boolean",
d="px",
e='"',
f="italic",
g="normal",
h="bold",
j="_applyItalic",
k="_applyBold",
m="Integer",
n="_applyFamily",
o="_applyLineHeight",
p="Array",
q="overline",
r="line-through",
s="qx.bom.Font",
t="Number",
u="_applyDecoration",
v=" ",
w="_applySize",
x=",";
qx.Class.define(s,
{extend:qx.core.Object,
construct:function(y,
z){arguments.callee.base.call(this);
if(y!==undefined){this.setSize(y);
}
if(z!==undefined){this.setFamily(z);
}},
statics:{fromString:function(A){var B=new qx.bom.Font();
var C=A.split(/\s+/);
var D=[];
var E;
for(var F=0;F<C.length;F++){switch(E=C[F]){case h:B.setBold(true);
break;
case f:B.setItalic(true);
break;
case b:B.setDecoration(b);
break;
default:var G=parseInt(E,
10);
if(G==E||qx.lang.String.contains(E,
d)){B.setSize(G);
}else{D.push(E);
}break;
}}
if(D.length>0){B.setFamily(D);
}return B;
},
fromConfig:function(H){var B=new qx.bom.Font;
B.set(H);
return B;
},
__bG:{fontFamily:a,
fontSize:a,
fontWeight:a,
fontStyle:a,
textDecoration:a,
lineHeight:1.2},
getDefaultStyles:function(){return this.__bG;
}},
properties:{size:{check:m,
nullable:true,
apply:w},
lineHeight:{check:t,
nullable:true,
apply:o},
family:{check:p,
nullable:true,
apply:n},
bold:{check:c,
nullable:true,
apply:k},
italic:{check:c,
nullable:true,
apply:j},
decoration:{check:[b,
r,
q],
nullable:true,
apply:u}},
members:{__bH:null,
__bI:null,
__bJ:null,
__bK:null,
__bL:null,
__bM:null,
_applySize:function(I,
J){this.__bH=I===null?null:I+d;
},
_applyLineHeight:function(I,
J){this.__bM=I===null?null:I;
},
_applyFamily:function(I,
J){var z=a;
for(var F=0,
K=I.length;F<K;F++){if(I[F].indexOf(v)>0){z+=e+I[F]+e;
}else{z+=I[F];
}
if(F!==K-1){z+=x;
}}this.__bI=z;
},
_applyBold:function(I,
J){this.__bJ=I===null?null:I?h:g;
},
_applyItalic:function(I,
J){this.__bK=I===null?null:I?f:g;
},
_applyDecoration:function(I,
J){this.__bL=I===null?null:I;
},
getStyles:function(){return {fontFamily:this.__bI,
fontSize:this.__bH,
fontWeight:this.__bJ,
fontStyle:this.__bK,
textDecoration:this.__bL,
lineHeight:this.__bM};
}}});
})();
(function(){var a="icon",
b="qx.theme.manager.Icon",
c="Theme",
d="_applyTheme",
e="singleton";
qx.Class.define(b,
{type:e,
extend:qx.core.Object,
properties:{theme:{check:c,
nullable:true,
apply:d}},
members:{_applyTheme:function(f,
g){var h=qx.util.AliasManager.getInstance();
f?h.add(a,
f.resource):h.remove(a);
}}});
})();
(function(){var a="string",
b="_applyAppearanceTheme",
c="__bN",
d="qx.theme.manager.Appearance",
e=":",
f="changeAppearanceTheme",
g="Theme",
h="__bO",
i="/",
j="singleton";
qx.Class.define(d,
{type:j,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this.__bN={};
this.__bO={};
},
properties:{appearanceTheme:{check:g,
nullable:true,
apply:b,
event:f}},
members:{__bP:{},
_applyAppearanceTheme:function(k,
l){},
__bQ:function(m,
n){var o=n.appearances;
var p=o[m];
if(!p){var q=i;
var r=[];
var s=m.split(q);
var t;
while(!p&&s.length>0){r.unshift(s.pop());
var u=s.join(q);
p=o[u];
if(p){t=p.alias||p;
if(typeof t===a){var v=t+q+r.join(q);
return this.__bQ(v,
n);
}}}return null;
}else if(typeof p===a){return this.__bQ(p,
n);
}else if(p.include&&!p.style){return this.__bQ(p.include,
n);
}return m;
},
styleFrom:function(m,
w,
n){if(!n){n=this.getAppearanceTheme();
}var x=this.__bO;
var y=x[m];
if(!y){y=x[m]=this.__bQ(m,
n);
}var p=n.appearances[y];
if(!p){this.warn("Missing appearance: "+m);
return null;
}if(!p.style){return null;
}var z=y;
if(w){var A=p.$$bits;
if(!A){A=p.$$bits={};
p.$$length=0;
}var B=0;
for(var C in w){if(A[C]==null){A[C]=1<<p.$$length++;
}B+=A[C];
}if(B>0){z+=e+B;
}}var D=this.__bN;
if(D[z]!==undefined){return D[z];
}if(!w){w=this.__bP;
}var E;
if(p.include||p.base){var F=p.style(w);
var G;
if(p.include){G=this.styleFrom(p.include,
w,
n);
}E={};
if(p.base){var H=this.styleFrom(y,
w,
p.base);
if(p.include){for(var I in H){if(G[I]===undefined&&F[I]===undefined){E[I]=H[I];
}}}else{for(var I in H){if(F[I]===undefined){E[I]=H[I];
}}}}if(p.include){for(var I in G){if(F[I]===undefined){E[I]=G[I];
}}}for(var I in F){E[I]=F[I];
}}else{E=p.style(w);
}return D[z]=E||null;
}},
destruct:function(){this._disposeFields(c,
h);
}});
})();
(function(){var b="other",
c="widgets",
d="fonts",
e="appearances",
f="qx.Theme",
g="]",
h="[Theme ",
j="colors",
k="decorations",
m="Theme",
n="meta",
o="borders",
p="icons";
qx.Class.define(f,
{statics:{define:function(q,
r){if(!r){var r={};
}
if(r.include&&!(r.include instanceof Array)){r.include=[r.include];
}{};
var s={$$type:m,
name:q,
title:r.title,
toString:this.genericToString};
if(r.extend){s.supertheme=r.extend;
}if(r.resource){s.resource=r.resource;
}s.basename=qx.Bootstrap.createNamespace(q,
s);
this.__bS(s,
r);
this.$$registry[q]=s;
if(r.include){for(var t=0,
u=r.include,
v=u.length;t<v;t++){this.include(s,
u[t]);
}}},
getAll:function(){return this.$$registry;
},
getByName:function(q){return this.$$registry[q];
},
isDefined:function(q){return this.getByName(q)!==undefined;
},
getTotalNumber:function(){return qx.lang.Object.getLength(this.$$registry);
},
genericToString:function(){return h+this.name+g;
},
__bR:function(r){for(var t=0,
w=this.__bT,
v=w.length;t<v;t++){if(r[w[t]]){return w[t];
}}},
__bS:function(s,
r){var x=this.__bR(r);
if(r.extend&&!x){x=r.extend.type;
}s.type=x||b;
if(!x){return;
}var y=function(){};
if(r.extend){y.prototype=new r.extend.$$clazz;
}var z=y.prototype;
var A=r[x];
for(var B in A){z[B]=A[B];
if(z[B].base){{};
z[B].base=r.extend;
}}s.$$clazz=y;
s[x]=new y;
},
$$registry:{},
__bT:[j,
o,
k,
d,
p,
c,
e,
n],
__bU:null,
__bV:null,
__bW:function(){},
patch:function(s,
C){var x=this.__bR(C);
if(x!==this.__bR(s)){throw new Error("The mixins '"+s.name+"' are not compatible '"+C.name+"'!");
}var A=C[x];
var z=s[x];
for(var D in A){z[D]=A[D];
}},
include:function(s,
C){var x=C.type;
if(x!==s.type){throw new Error("The mixins '"+s.name+"' are not compatible '"+C.name+"'!");
}var A=C[x];
var z=s[x];
for(var D in A){if(z[D]!==undefined){throw new Error("It is not allowed to overwrite the key '"+D+"' of theme '"+s.name+"' by mixin theme '"+C.name+"'.");
}z[D]=A[D];
}}}});
})();
(function(){var a="qx.event.handler.UserAction",
b="__bX",
c="__bY";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(d){arguments.callee.base.call(this);
this.__bX=d;
this.__bY=d.getWindow();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{useraction:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_WINDOW,
IGNORE_CAN_HANDLE:true},
members:{canHandleEvent:function(e,
f){},
registerEvent:function(e,
f,
g){},
unregisterEvent:function(e,
f,
g){}},
destruct:function(){this._disposeFields(b,
c);
},
defer:function(h){qx.event.Registration.addHandler(h);
}});
})();
(function(){var a="__ca",
b="__cb",
c="qx.util.DeferredCallManager",
d="singleton";
qx.Class.define(c,
{extend:qx.core.Object,
type:d,
construct:function(){this.__ca={};
this.__cb=qx.lang.Function.bind(this.__cf,
this);
this.__cc=false;
},
members:{__cd:null,
__ce:null,
__ca:null,
__cc:null,
schedule:function(e){if(this.__cd==null){this.__cd=window.setTimeout(this.__cb,
0);
}var f=e.toHashCode();
if(this.__ce&&this.__ce[f]){return;
}this.__ca[f]=e;
this.__cc=true;
},
cancel:function(e){var f=e.toHashCode();
if(this.__ce&&this.__ce[f]){this.__ce[f]=null;
return;
}delete this.__ca[f];
if(qx.lang.Object.isEmpty(this.__ca)&&this.__cd!=null){window.clearTimeout(this.__cd);
this.__cd=null;
}},
__cf:function(){this.__cd=null;
while(this.__cc){this.__ce=qx.lang.Object.copy(this.__ca);
this.__ca={};
this.__cc=false;
for(var g in this.__ce){var h=this.__ce[g];
if(h){this.__ce[g]=null;
h.call();
}}}this.__ce=null;
}},
destruct:function(){if(this.__cd!=null){window.clearTimeout(this.__cd);
}this._disposeFields(b,
a);
}});
})();
(function(){var a="qx.util.DeferredCall",
b="__ci",
c="__cg",
d="__ch";
qx.Class.define(a,
{extend:qx.core.Object,
construct:function(e,
f){arguments.callee.base.call(this);
this.__cg=e;
this.__ch=f||null;
this.__ci=qx.util.DeferredCallManager.getInstance();
},
members:{__cg:null,
__ch:null,
__ci:null,
cancel:function(){this.__ci.cancel(this);
},
schedule:function(){this.__ci.schedule(this);
},
call:function(){this.__ch?this.__cg.apply(this.__ch):this.__cg();
}},
destruct:function(e,
f){this.cancel();
this._disposeFields(d,
c,
b);
}});
})();
(function(){var c="element",
d="qx.client",
e="div",
f="-",
g="",
h="mshtml",
j="qx.html.Element",
k="__cv",
m="__cr",
n="focus",
o="__cl",
p="__cB",
q="evt-",
r="__ck",
s="capture",
t="__cz",
u="__cq",
v="__cA",
w="tabIndex",
z="__cm",
A="_element",
B="activate",
C="__cC",
D="none",
E="-capture",
F="__cw";
qx.Class.define(j,
{extend:qx.core.Object,
construct:function(G){arguments.callee.base.call(this);
this._nodeName=G||e;
},
statics:{DEBUG:false,
_modified:{},
_visibility:{},
_scroll:{},
_actions:{},
_supportedActions:[B,
n,
s],
_scheduleFlush:function(H){qx.html.Element.__cD.schedule();
},
_mshtmlVisibilitySort:qx.core.Variant.select(d,
{"mshtml":function(I,
J){var K=I._element;
var L=J._element;
if(K.contains(L)){return 1;
}
if(L.contains(K)){return -1;
}return 0;
},
"default":null}),
flush:function(){var M;
{};
var N=[];
var O=this._modified;
for(var P in O){M=O[P];
if(M.__cn()){if(M._element&&qx.dom.Hierarchy.isRendered(M._element)){N.push(M);
}else{{};
M.__cj();
}delete O[P];
}}
for(var Q=0,
R=N.length;Q<R;Q++){M=N[Q];
{};
M.__cj();
}var S=this._visibility;
if(qx.core.Variant.isSet(d,
h)){var T=[];
for(var P in S){T.push(S[P]);
}if(T.length>1){T.sort(this._mshtmlVisibilitySort);
S=this._visibility={};
for(var Q=0;Q<T.length;Q++){M=T[Q];
S[M.$$hash]=M;
}}}
for(var P in S){M=S[P];
{};
M._element.style.display=M._visible?g:D;
delete S[P];
}var U=this._scroll;
for(var P in U){M=U[P];
var V=M._element;
if(V&&V.offsetWidth){var W=true;
if(M.__cx!=null){M._element.scrollLeft=M.__cx;
delete M.__cx;
}if(M.__cy!=null){M._element.scrollTop=M.__cy;
delete M.__cy;
}var X=M.__cv;
if(X!=null){var Y=X.element.getDomElement();
if(Y&&Y.offsetWidth){qx.bom.element.Scroll.intoViewX(Y,
V,
X.align);
delete M.__cv;
}else{W=false;
}}var ba=M.__cw;
if(ba!=null){var Y=ba.element.getDomElement();
if(Y&&Y.offsetWidth){qx.bom.element.Scroll.intoViewY(Y,
V,
ba.align);
delete M.__cw;
}else{W=false;
}}if(W){delete U[P];
}}}var bb=this._actions;
var bc=this._supportedActions;
var bd,
be;
for(var Q=0,
R=bc.length;Q<R;Q++){bd=bc[Q];
if(bb[bd]){be=bb[bd]._element;
if(be){qx.bom.Element[bd](be);
}delete bb[bd];
}}qx.event.handler.Appear.refresh();
}},
members:{_element:null,
_root:false,
_included:true,
_visible:true,
_scheduleChildrenUpdate:function(){if(this._modifiedChildren){return;
}this._modifiedChildren=true;
qx.html.Element._modified[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
},
_createDomElement:function(){return qx.bom.Element.create(this._nodeName);
},
__cj:function(){{};
var bf=this.__cr;
if(bf){var bg=bf.length;
var Y;
for(var Q=0;Q<bg;Q++){Y=bf[Q];
if(Y._visible&&Y._included&&!Y._element){Y.__cj();
}}}
if(!this._element){this._element=this._createDomElement();
this._element.$$hash=this.$$hash;
this._copyData(false);
if(bf&&bg>0){this._insertChildren();
}}else{this._syncData();
if(this._modifiedChildren){this._syncChildren();
}}delete this._modifiedChildren;
},
_insertChildren:function(){var bf=this.__cr;
var bg=bf.length;
var Y;
if(bg>2){var bh=document.createDocumentFragment();
for(var Q=0;Q<bg;Q++){Y=bf[Q];
if(Y._element&&Y._included){bh.appendChild(Y._element);
}}this._element.appendChild(bh);
}else{var bh=this._element;
for(var Q=0;Q<bg;Q++){Y=bf[Q];
if(Y._element&&Y._included){bh.appendChild(Y._element);
}}}},
_syncChildren:function(){var bi=qx.core.ObjectRegistry;
var bj=this.__cr;
var bk=bj.length;
var bm;
var bn;
var bo=this._element;
var bp=bo.childNodes;
var bq=0;
var br;
var bs;
for(var Q=bp.length-1;Q>=0;Q--){br=bp[Q];
bn=bi.fromHashCode(br.$$hash);
if(!bn||!bn._included||bn.__cq!==this){bo.removeChild(br);
{};
}}for(var Q=0;Q<bk;Q++){bm=bj[Q];
if(bm._included){bn=bm._element;
br=bp[bq];
if(!bn){continue;
}if(bn!=br){if(br){bo.insertBefore(bn,
br);
}else{bo.appendChild(bn);
}{};
}bq++;
}}{};
},
_copyData:function(bt){var V=this._element;
var bu=this.__cA;
if(bu){var bv=qx.bom.element.Attribute;
for(var bw in bu){bv.set(V,
bw,
bu[bw]);
}}var bu=this.__cz;
if(bu){var bx=qx.bom.element.Style;
if(bt){for(var bw in bu){bx.set(V,
bw,
bu[bw]);
}}else{bx.setCss(V,
bx.compile(bu));
}}var bu=this.__cB;
if(bu){for(var bw in bu){this._applyProperty(bw,
bu[bw]);
}}var bu=this.__cC;
if(bu){qx.event.Registration.getManager(V).importListeners(V,
bu);
delete this.__cC;
}},
_syncData:function(){var V=this._element;
var bv=qx.bom.element.Attribute;
var bx=qx.bom.element.Style;
var by=this.__ck;
if(by){var bu=this.__cA;
if(bu){var bz;
for(var bw in by){bz=bu[bw];
if(bz!==undefined){bv.set(V,
bw,
bz);
}else{bv.reset(V,
bw);
}}}this.__ck=null;
}var by=this.__cl;
if(by){var bu=this.__cz;
if(bu){var bz;
for(var bw in by){bz=bu[bw];
if(bz!==undefined){bx.set(V,
bw,
bz);
}else{bx.reset(V,
bw);
}}}this.__cl=null;
}var by=this.__cm;
if(by){var bu=this.__cB;
if(bu){var bz;
for(var bw in by){this._applyProperty(bw,
bu[bw]);
}}this.__cm=null;
}},
__cn:function(){var bA=this;
while(bA){if(bA._root){return true;
}
if(!bA._included||!bA._visible){return false;
}bA=bA.__cq;
}return false;
},
__co:function(bB,
bC,
bD,
bE){var bF=qx.core.ObjectRegistry;
var bG=q+bB+f+bF.toHashCode(bC);
if(bD){bG+=f+bF.toHashCode(bD);
}
if(bE){bG+=E;
}return bG;
},
__cp:function(Y){if(Y.__cq===this){throw new Error("Child is already in: "+Y);
}
if(Y._root){throw new Error("Root elements could not be inserted into other ones.");
}if(Y.__cq){Y.__cq.remove(Y);
}Y.__cq=this;
if(!this.__cr){this.__cr=[];
}if(this._element){this._scheduleChildrenUpdate();
}},
__cs:function(Y){if(Y.__cq!==this){throw new Error("Has no child: "+Y);
}if(this._element){this._scheduleChildrenUpdate();
}delete Y.__cq;
},
__ct:function(Y){if(Y.__cq!==this){throw new Error("Has no child: "+Y);
}if(this._element){this._scheduleChildrenUpdate();
}},
getChildren:function(){return this.__cr||null;
},
getChild:function(bH){var bf=this.__cr;
return bf&&bf[bH]||null;
},
hasChildren:function(){var bf=this.__cr;
return bf&&bf[0]!==undefined;
},
indexOf:function(Y){var bf=this.__cr;
return bf?bf.indexOf(Y):-1;
},
hasChild:function(Y){var bf=this.__cr;
return bf&&bf.indexOf(Y)!==-1;
},
add:function(bI){if(arguments[1]){for(var Q=0,
R=arguments.length;Q<R;Q++){this.__cp(arguments[Q]);
}this.__cr.push.apply(this.__cr,
arguments);
}else{this.__cp(bI);
this.__cr.push(bI);
}return this;
},
addAt:function(Y,
bH){this.__cp(Y);
qx.lang.Array.insertAt(this.__cr,
Y,
bH);
return this;
},
remove:function(bJ){var bf=this.__cr;
if(!bf){return;
}
if(arguments[1]){var Y;
for(var Q=0,
R=arguments.length;Q<R;Q++){Y=arguments[Q];
this.__cs(Y);
qx.lang.Array.remove(bf,
Y);
}}else{this.__cs(bJ);
qx.lang.Array.remove(bf,
bJ);
}return this;
},
removeAt:function(bH){var bf=this.__cr;
if(!bf){throw new Error("Has no children!");
}var Y=bf[bH];
if(!Y){throw new Error("Has no child at this position!");
}this.__cs(Y);
qx.lang.Array.removeAt(this.__cr,
bH);
return this;
},
removeAll:function(){var bf=this.__cr;
if(bf){for(var Q=0,
R=bf.length;Q<R;Q++){this.__cs(bf[Q]);
}bf.length=0;
}return this;
},
getParent:function(){return this.__cq||null;
},
insertInto:function(bK,
bH){bK.__cp(this);
if(bH==null){bK.__cr.push(this);
}else{qx.lang.Array.insertAt(this.__cr,
this,
bH);
}return this;
},
insertBefore:function(bL){var bK=bL.__cq;
bK.__cp(this);
qx.lang.Array.insertBefore(bK.__cr,
this,
bL);
return this;
},
insertAfter:function(bL){var bK=bL.__cq;
bK.__cp(this);
qx.lang.Array.insertAfter(bK.__cr,
this,
bL);
return this;
},
moveTo:function(bH){var bK=this.__cq;
bK.__ct(this);
var bM=bK.__cr.indexOf(this);
if(bM===bH){throw new Error("Could not move to same index!");
}else if(bM<bH){bH--;
}qx.lang.Array.removeAt(bK.__cr,
bM);
qx.lang.Array.insertAt(bK.__cr,
this,
bH);
return this;
},
moveBefore:function(bL){var bK=this.__cq;
return this.moveTo(bK.__cr.indexOf(bL));
},
moveAfter:function(bL){var bK=this.__cq;
return this.moveTo(bK.__cr.indexOf(bL)+1);
},
free:function(){var bK=this.__cq;
if(!bK){throw new Error("Has no parent to remove from.");
}
if(!bK.__cr){return;
}bK.__cs(this);
qx.lang.Array.remove(bK.__cr,
this);
return this;
},
getDomElement:function(){return this._element||null;
},
getNodeName:function(){return this._nodeName;
},
useMarkup:function(bN){if(this._element){throw new Error("Could not overwrite existing element!");
}if(qx.core.Variant.isSet(d,
h)){var bO=document.createElement(e);
}else{var bO=qx.html.Element.__cu;
if(!bO){bO=qx.html.Element.__cu=document.createElement(e);
}}bO.innerHTML=bN;
this._element=bO.firstChild;
this._element.$$hash=this.$$hash;
this._copyData(true);
return this._element;
},
useElement:function(V){if(this._element){throw new Error("Could not overwrite existing element!");
}this._element=V;
this._element.$$hash=this.$$hash;
this._copyData(true);
},
isFocusable:function(){var bP=this.getAttribute(w);
if(bP>=1){return true;
}var bQ=qx.event.handler.Focus.FOCUSABLE_ELEMENTS;
if(bP>=0&&bQ[this._nodeName]){return true;
}return false;
},
isNativelyFocusable:function(){return !!qx.event.handler.Focus.FOCUSABLE_ELEMENTS[this._nodeName];
},
include:function(){if(this._included){return;
}delete this._included;
if(this.__cq){this.__cq._scheduleChildrenUpdate();
}return this;
},
exclude:function(){if(!this._included){return;
}this._included=false;
if(this.__cq){this.__cq._scheduleChildrenUpdate();
}return this;
},
isIncluded:function(){return this._included===true;
},
show:function(){if(this._visible){return;
}
if(this._element){qx.html.Element._visibility[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}if(this.__cq){this.__cq._scheduleChildrenUpdate();
}delete this._visible;
},
hide:function(){if(!this._visible){return;
}
if(this._element){qx.html.Element._visibility[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}this._visible=false;
},
isVisible:function(){return this._visible===true;
},
scrollChildIntoViewX:function(V,
bR,
bS){var bT=this._element;
var bU=V.getDomElement();
if(bS!==false&&bT&&bT.offsetWidth&&bU&&bU.offsetWidth){qx.bom.element.Scroll.intoViewX(bU,
bT,
bR);
}else{this.__cv={element:V,
align:bR};
qx.html.Element._scroll[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}delete this.__cx;
},
scrollChildIntoViewY:function(V,
bR,
bS){var bT=this._element;
var bU=V.getDomElement();
if(bS!==false&&bT&&bT.offsetWidth&&bU&&bU.offsetWidth){qx.bom.element.Scroll.intoViewY(bU,
bT,
bR);
}else{this.__cw={element:V,
align:bR};
qx.html.Element._scroll[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}delete this.__cy;
},
scrollToX:function(bV,
bW){var bT=this._element;
if(bW!==true&&bT&&bT.offsetWidth){bT.scrollLeft=bV;
}else{this.__cx=bV;
qx.html.Element._scroll[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}delete this.__cv;
},
getScrollX:function(){var bT=this._element;
if(bT){return bT.scrollLeft;
}return this.__cx||0;
},
scrollToY:function(bX,
bW){var bT=this._element;
if(bW!==true&&bT&&bT.offsetWidth){bT.scrollTop=bX;
}else{this.__cy=bX;
qx.html.Element._scroll[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}delete this.__cw;
},
getScrollY:function(){var bT=this._element;
if(bT){return bT.scrollTop;
}return this.__cy||0;
},
getSelection:function(){var bY=this._element;
if(bY){return qx.bom.Selection.get(bY);
}return null;
},
getSelectionLength:function(){var bY=this._element;
if(bY){return qx.bom.Selection.getLength(bY);
}return null;
},
setSelection:function(ca,
cb){var bY=this._element;
if(bY){qx.bom.Selection.set(bY,
ca,
cb);
}},
clearSelection:function(){var bY=this._element;
if(bY){qx.bom.Selection.clear(bY);
}},
focus:function(){var bY=this._element;
if(bY){return qx.bom.Element.focus(bY);
}qx.html.Element._actions.focus=this;
qx.html.Element._scheduleFlush(c);
},
blur:function(){var bY=this._element;
if(bY){qx.bom.Element.blur(bY);
}},
activate:function(){var bY=this._element;
if(bY){return qx.bom.Element.activate(bY);
}qx.html.Element._actions.activate=this;
qx.html.Element._scheduleFlush(c);
},
deactivate:function(){var bY=this._element;
if(bY){qx.bom.Element.deactivate(bY);
}},
capture:function(){var bY=this._element;
if(bY){return qx.bom.Element.capture(bY);
}qx.html.Element._actions.capture=this;
qx.html.Element._scheduleFlush(c);
},
releaseCapture:function(){var bY=this._element;
if(bY){qx.bom.Element.releaseCapture(bY);
}},
setStyle:function(bw,
bz,
bS){if(!this.__cz){this.__cz={};
}
if(this.__cz[bw]==bz){return;
}
if(bz==null){delete this.__cz[bw];
}else{this.__cz[bw]=bz;
}if(this._element){if(bS){qx.bom.element.Style.set(this._element,
bw,
bz);
return this;
}if(!this.__cl){this.__cl={};
}this.__cl[bw]=true;
qx.html.Element._modified[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}return this;
},
setStyles:function(cc,
bS){for(var bw in cc){this.setStyle(bw,
cc[bw],
bS);
}return this;
},
removeStyle:function(bw,
bS){this.setStyle(bw,
null,
bS);
},
getStyle:function(bw){return this.__cz?this.__cz[bw]:null;
},
setAttribute:function(bw,
bz,
bS){if(!this.__cA){this.__cA={};
}
if(this.__cA[bw]==bz){return;
}
if(bz==null){delete this.__cA[bw];
}else{this.__cA[bw]=bz;
}if(this._element){if(bS){qx.bom.element.Attribute.set(this._element,
bw,
bz);
return this;
}if(!this.__ck){this.__ck={};
}this.__ck[bw]=true;
qx.html.Element._modified[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}return this;
},
setAttributes:function(cc,
bS){for(var bw in cc){this.setAttribute(bw,
cc[bw],
bS);
}return this;
},
removeAttribute:function(bw,
bS){this.setAttribute(bw,
null,
bS);
},
getAttribute:function(bw){return this.__cA?this.__cA[bw]:null;
},
_applyProperty:function(cd,
bz){},
_setProperty:function(bw,
bz,
bS){if(!this.__cB){this.__cB={};
}
if(this.__cB[bw]==bz){return;
}
if(bz==null){delete this.__cB[bw];
}else{this.__cB[bw]=bz;
}if(this._element){if(bS){this._applyProperty(bw,
bz);
return this;
}if(!this.__cm){this.__cm={};
}this.__cm[bw]=true;
qx.html.Element._modified[this.$$hash]=this;
qx.html.Element._scheduleFlush(c);
}return this;
},
_removeProperty:function(bw,
bS){this._setProperty(bw,
null,
bS);
},
_getProperty:function(bw){return this.__cB?this.__cB[bw]:null;
},
addListener:function(bB,
bC,
bD,
bE){if(this.isDisposed()){return;
}var ce;
if(this._element){qx.event.Registration.addListener(this._element,
bB,
bC,
bD,
bE);
}else{if(!this.__cC){this.__cC={};
}var bw=this.__co(bB,
bC,
bD,
bE);
if(this.__cC[bw]){this.warn("A listener of this configuration does already exist!");
return false;
}this.__cC[bw]={type:bB,
listener:bC,
self:bD,
capture:bE};
}return this;
},
removeListener:function(bB,
bC,
bD,
bE){if(this.isDisposed()){return;
}var ce;
if(this._element){qx.event.Registration.removeListener(this._element,
bB,
bC,
bD,
bE);
}else{var bw=this.__co(bB,
bC,
bD,
bE);
if(!this.__cC||!this.__cC[bw]){this.warn("A listener of this configuration does not exist!");
return false;
}delete this.__cC[bw];
}return this;
},
hasListener:function(bB,
bE){throw new Error("hasListener() needs implementation!");
}},
defer:function(cf){cf.__cD=new qx.util.DeferredCall(cf.flush,
cf);
},
destruct:function(){var bY=this._element;
if(bY){qx.event.Registration.getManager(bY).removeAllListeners(bY);
bY.$$hash=g;
}
if(!qx.core.ObjectRegistry.inShutDown){var bK=this.__cq;
if(bK&&!bK.$$disposed){bK.remove(this);
}}this._disposeArray(m);
this._disposeFields(v,
t,
C,
p,
r,
o,
z,
A,
u,
k,
F);
}});
})();
(function(){var a="qx.ui.core.queue.Manager",
b="useraction";
qx.Class.define(a,
{statics:{__cE:false,
__cF:{},
scheduleFlush:function(c){var d=qx.ui.core.queue.Manager;
d.__cF[c]=true;
if(!d.__cE){d.__cH.schedule();
d.__cE=true;
}},
flush:function(){var d=qx.ui.core.queue.Manager;
if(d.__cG){return;
}d.__cG=true;
d.__cH.cancel();
var e=d.__cF;
while(e.widget||e.appearance||e.decorator||e.layout){if(e.widget){delete e.widget;
qx.ui.core.queue.Widget.flush();
if(e.widget){continue;
}}
if(e.appearance){delete e.appearance;
qx.ui.core.queue.Appearance.flush();
if(e.appearance){continue;
}}
if(e.layout){delete e.layout;
qx.ui.core.queue.Layout.flush();
}}qx.ui.core.queue.Manager.__cE=false;
if(e.element){delete e.element;
qx.html.Element.flush();
}
if(e.dispose){delete e.dispose;
qx.ui.core.queue.Dispose.flush();
}d.__cG=false;
}},
defer:function(f){f.__cH=new qx.util.DeferredCall(f.flush);
qx.html.Element._scheduleFlush=f.scheduleFlush;
qx.event.Registration.addListener(window,
b,
f.flush);
}});
})();
(function(){var a="qx.client",
b="qx.dom.Hierarchy",
c="previousSibling",
d="*",
e="nextSibling",
f="parentNode";
qx.Class.define(b,
{statics:{getNodeIndex:function(g){var h=0;
while(g&&(g=g.previousSibling)){h++;
}return h;
},
getElementIndex:function(i){var h=0;
var j=qx.dom.Node.ELEMENT;
while(i&&(i=i.previousSibling)){if(i.nodeType==j){h++;
}}return h;
},
getNextElementSibling:function(i){while(i&&(i=i.nextSibling)&&!qx.dom.Node.isElement(i)){continue;
}return i||null;
},
getPreviousElementSibling:function(i){while(i&&(i=i.previousSibling)&&!qx.dom.Node.isElement(i)){continue;
}return i||null;
},
contains:qx.core.Variant.select(a,
{"webkit|mshtml|opera":function(i,
k){if(qx.dom.Node.isDocument(i)){var l=qx.dom.Node.getDocument(k);
return i&&l==i;
}else if(qx.dom.Node.isDocument(k)){return false;
}else{return i.contains(k);
}},
"gecko":function(i,
k){return !!(i.compareDocumentPosition(k)&16);
},
"default":function(i,
k){while(k){if(i==k){return true;
}k=k.parentNode;
}return false;
}}),
isRendered:function(i){if(!i.offsetParent){return false;
}var l=i.ownerDocument||i.document;
if(l.body.contains){return l.body.contains(i);
}if(l.compareDocumentPosition){return !!(l.compareDocumentPosition(i)&16);
}throw new Error("Missing support for isRendered()!");
},
isDescendantOf:function(i,
m){return this.contains(m,
i);
},
getCommonParent:qx.core.Variant.select(a,
{"mshtml|opera":function(n,
o){if(n===o){return n;
}
while(n){if(n.contains(o)){return n;
}n=n.parentNode;
}return null;
},
"default":function(n,
o){if(n===o){return n;
}var p={};
var q=qx.core.ObjectRegistry;
var r,
s;
while(n||o){if(n){r=q.toHashCode(n);
if(p[r]){return p[r];
}p[r]=n;
n=n.parentNode;
}
if(o){s=q.toHashCode(o);
if(p[s]){return p[s];
}p[s]=o;
o=o.parentNode;
}}return null;
}}),
getAncestors:function(i){return this._recursivelyCollect(i,
f);
},
getChildElements:function(i){i=i.firstChild;
if(!i){return [];
}var t=this.getNextSiblings(i);
t.unshift(i);
return t;
},
getDescendants:function(i){return qx.lang.Array.fromCollection(i.getElementsByTagName(d));
},
getFirstDescendant:function(i){i=i.firstChild;
while(i&&i.nodeType!=1){i=i.nextSibling;
}return i;
},
getLastDescendant:function(i){i=i.lastChild;
while(i&&i.nodeType!=1){i=i.previousSibling;
}return i;
},
getPreviousSiblings:function(i){return this._recursivelyCollect(i,
c);
},
getNextSiblings:function(i){return this._recursivelyCollect(i,
e);
},
_recursivelyCollect:function(i,
u){var v=[];
while(i=i[u]){if(i.nodeType==1){v.push(i);
}}return v;
},
getSiblings:function(i){return this.getPreviousSiblings(i).reverse().concat(this.getNextSiblings(i));
},
isEmpty:function(i){i=i.firstChild;
while(i){if(i.nodeType===qx.dom.Node.ELEMENT||i.nodeType===qx.dom.Node.TEXT){return false;
}i=i.nextSibling;
}return true;
},
cleanWhitespace:function(i){var g=i.firstChild;
while(g){var w=g.nextSibling;
if(g.nodeType==3&&!/\S/.test(g.nodeValue)){i.removeChild(g);
}g=w;
}}}});
})();
(function(){var a="visible",
b="scroll",
c="borderBottomWidth",
d="borderTopWidth",
e="left",
f="borderLeftWidth",
g="bottom",
h="top",
i="right",
j="qx.bom.element.Scroll",
k="borderRightWidth";
qx.Class.define(j,
{statics:{intoViewX:function(l,
m,
n){var o=l.parentNode;
var p=qx.dom.Node.getDocument(l);
var q=p.body;
var r,
s,
t;
var u,
v,
w;
var x,
y,
z;
var A,
B,
C,
D;
var E,
F,
G;
var H=n===e;
var I=n===i;
m=m?m.parentNode:p;
while(o&&o!=m){if(o.scrollWidth>o.clientWidth&&(o===q||qx.bom.element.Overflow.getY(o)!=a)){if(o===q){s=o.scrollLeft;
t=s+qx.bom.Viewport.getWidth();
u=qx.bom.Viewport.getWidth();
v=o.clientWidth;
w=o.scrollWidth;
x=0;
y=0;
z=0;
}else{r=qx.bom.element.Location.get(o);
s=r.left;
t=r.right;
u=o.offsetWidth;
v=o.clientWidth;
w=o.scrollWidth;
x=parseInt(qx.bom.element.Style.get(o,
f),
10)||0;
y=parseInt(qx.bom.element.Style.get(o,
k),
10)||0;
z=u-v-x-y;
}A=qx.bom.element.Location.get(l);
B=A.left;
C=A.right;
D=l.offsetWidth;
E=B-s-x;
F=C-t+y;
G=0;
if(H){G=E;
}else if(I){G=F+z;
}else if(E<0||D>v){G=E;
}else if(F>0){G=F+z;
}o.scrollLeft+=G;
if(qx.bom.client.Engine.GECKO){qx.event.Registration.fireNonBubblingEvent(o,
b);
}}
if(o===q){break;
}o=o.parentNode;
}},
intoViewY:function(l,
m,
n){var o=l.parentNode;
var p=qx.dom.Node.getDocument(l);
var q=p.body;
var r,
J,
K;
var L,
M,
N;
var O,
P,
Q;
var A,
R,
S,
T;
var U,
V,
G;
var W=n===h;
var X=n===g;
m=m?m.parentNode:p;
while(o&&o!=m){if(o.scrollHeight>o.clientHeight&&(o===q||qx.bom.element.Overflow.getY(o)!=a)){if(o===q){J=o.scrollTop;
K=J+qx.bom.Viewport.getHeight();
L=qx.bom.Viewport.getHeight();
M=o.clientHeight;
N=o.scrollHeight;
O=0;
P=0;
Q=0;
}else{r=qx.bom.element.Location.get(o);
J=r.top;
K=r.bottom;
L=o.offsetHeight;
M=o.clientHeight;
N=o.scrollHeight;
O=parseInt(qx.bom.element.Style.get(o,
d),
10)||0;
P=parseInt(qx.bom.element.Style.get(o,
c),
10)||0;
Q=L-M-O-P;
}A=qx.bom.element.Location.get(l);
R=A.top;
S=A.bottom;
T=l.offsetHeight;
U=R-J-O;
V=S-K+P;
G=0;
if(W){G=U;
}else if(X){G=V+Q;
}else if(U<0||T>M){G=U;
}else if(V>0){G=V+Q;
}o.scrollTop+=G;
if(qx.bom.client.Engine.GECKO){qx.event.Registration.fireNonBubblingEvent(o,
b);
}}
if(o===q){break;
}o=o.parentNode;
}},
intoView:function(l,
m,
Y,
ba){this.intoViewX(l,
m,
Y);
this.intoViewY(l,
m,
ba);
}}});
})();
(function(){var a="",
b="qx.client",
d="hidden",
e="-moz-scrollbars-none",
f="overflow",
g=";",
h="overflowY",
i=":",
j="overflowX",
k="overflow:",
l="none",
m="scroll",
n="borderLeftStyle",
o="borderRightStyle",
p="div",
q="borderRightWidth",
r="overflow-y",
u="borderLeftWidth",
v="-moz-scrollbars-vertical",
w="100px",
x="qx.bom.element.Overflow",
y="overflow-x";
qx.Class.define(x,
{statics:{__cI:null,
getScrollbarWidth:function(){if(this.__cI!==null){return this.__cI;
}var z=qx.bom.element.Style;
var A=function(B,
C){return parseInt(z.get(B,
C))||0;
};
var D=function(B){return (z.get(B,
o)==l?0:A(B,
q));
};
var E=function(B){return (z.get(B,
n)==l?0:A(B,
u));
};
var F=qx.core.Variant.select(b,
{"mshtml":function(B){if(z.get(B,
h)==d||B.clientWidth==0){return D(B);
}return Math.max(0,
B.offsetWidth-B.clientLeft-B.clientWidth);
},
"default":function(B){if(B.clientWidth==0){var G=z.get(B,
f);
var H=(G==m||G==v?16:0);
return Math.max(0,
D(B)+H);
}return Math.max(0,
(B.offsetWidth-B.clientWidth-E(B)));
}});
var I=function(B){return F(B)-D(B);
};
var J=document.createElement(p);
var K=J.style;
K.height=K.width=w;
K.overflow=m;
document.body.appendChild(J);
var L=I(J);
this.__cI=L?L:16;
document.body.removeChild(J);
return this.__cI;
},
_compile:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(M,
N){if(N==d){N=e;
}return k+N+g;
}:
function(M,
N){return M+i+N+g;
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(M,
N){return k+N+g;
}:
function(M,
N){return M+i+N+g;
},
"default":function(M,
N){return M+i+N+g;
}}),
compileX:function(N){return this._compile(y,
N);
},
compileY:function(N){return this._compile(r,
N);
},
getX:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O,
P){var Q=qx.bom.element.Style.get(O,
f,
P,
false);
if(Q===e){Q=d;
}return Q;
}:
function(O,
P){return qx.bom.element.Style.get(O,
j,
P,
false);
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
P){return qx.bom.element.Style.get(O,
f,
P,
false);
}:
function(O,
P){return qx.bom.element.Style.get(O,
j,
P,
false);
},
"default":function(O,
P){return qx.bom.element.Style.get(O,
j,
P,
false);
}}),
setX:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O,
N){if(N==d){N=e;
}O.style.overflow=N;
}:
function(O,
N){O.style.overflowX=N;
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
N){O.style.overflow=N;
}:
function(O,
N){O.style.overflowX=N;
},
"default":function(O,
N){O.style.overflowX=N;
}}),
resetX:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O){O.style.overflow=a;
}:
function(O){O.style.overflowX=a;
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
N){O.style.overflow=a;
}:
function(O,
N){O.style.overflowX=a;
},
"default":function(O){O.style.overflowX=a;
}}),
getY:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O,
P){var Q=qx.bom.element.Style.get(O,
f,
P,
false);
if(Q===e){Q=d;
}return Q;
}:
function(O,
P){return qx.bom.element.Style.get(O,
h,
P,
false);
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
P){return qx.bom.element.Style.get(O,
f,
P,
false);
}:
function(O,
P){return qx.bom.element.Style.get(O,
h,
P,
false);
},
"default":function(O,
P){return qx.bom.element.Style.get(O,
h,
P,
false);
}}),
setY:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O,
N){if(N===d){N=e;
}O.style.overflow=N;
}:
function(O,
N){O.style.overflowY=N;
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
N){O.style.overflow=N;
}:
function(O,
N){O.style.overflowY=N;
},
"default":function(O,
N){O.style.overflowY=N;
}}),
resetY:qx.core.Variant.select(b,
{"gecko":qx.bom.client.Engine.VERSION<
1.8?
function(O){O.style.overflow=a;
}:
function(O){O.style.overflowY=a;
},
"opera":qx.bom.client.Engine.VERSION<
9.5?
function(O,
N){O.style.overflow=a;
}:
function(O,
N){O.style.overflowY=a;
},
"default":function(O){O.style.overflowY=a;
}})}});
})();
(function(){var a="",
b="qx.client",
c="boxSizing",
d="cursor",
e="opacity",
f="clip",
g="overflowY",
h="overflowX",
i="appearance",
j="style",
k="px",
l="-webkit-appearance",
m="user-select",
n="userSelect",
o="styleFloat",
p="-webkit-user-select",
q="-moz-appearance",
r="pixelHeight",
s="MozAppearance",
t=":",
u="pixelTop",
v="pixelLeft",
w="text-overflow",
x="-moz-user-select",
y="MozUserSelect",
z="qx.bom.element.Style",
A="WebkitUserSelect",
B="-o-text-overflow",
C="pixelRight",
D="pixelWidth",
E="pixelBottom",
F=";",
G="cssFloat",
H="WebkitAppearance";
qx.Class.define(z,
{statics:{__cJ:{styleNames:{"float":qx.core.Variant.select(b,
{"mshtml":o,
"default":G}),
"appearance":qx.core.Variant.select(b,
{"gecko":s,
"webkit":H,
"default":i}),
"userSelect":qx.core.Variant.select(b,
{"gecko":y,
"webkit":A,
"default":n})},
cssNames:{"appearance":qx.core.Variant.select(b,
{"gecko":q,
"webkit":l,
"default":i}),
"userSelect":qx.core.Variant.select(b,
{"gecko":x,
"webkit":p,
"default":m}),
"textOverflow":qx.core.Variant.select(b,
{"opera":B,
"default":w})},
mshtmlPixel:{width:D,
height:r,
left:v,
right:C,
top:u,
bottom:E},
special:{clip:1,
cursor:1,
opacity:1,
boxSizing:1,
overflowX:1,
overflowY:1}},
__cK:{},
compile:function(I){var J=[];
var K=this.__cJ;
var L=K.special;
var M=K.cssNames;
var N=this.__cK;
var O=qx.lang.String;
var P,
Q,
R;
for(P in I){R=I[P];
if(R==null){continue;
}P=M[P]||P;
if(L[P]){switch(P){case f:J.push(qx.bom.element.Clip.compile(R));
break;
case d:J.push(qx.bom.element.Cursor.compile(R));
break;
case e:J.push(qx.bom.element.Opacity.compile(R));
break;
case c:J.push(qx.bom.element.BoxSizing.compile(R));
break;
case h:J.push(qx.bom.element.Overflow.compileX(R));
break;
case g:J.push(qx.bom.element.Overflow.compileY(R));
break;
}}else{Q=N[P];
if(!Q){Q=N[P]=O.hyphenate(P);
}J.push(Q,
t,
R,
F);
}}return J.join(a);
},
setCss:qx.core.Variant.select(b,
{"mshtml":function(S,
R){S.style.cssText=R;
},
"default":function(S,
R){S.setAttribute(j,
R);
}}),
getCss:qx.core.Variant.select(b,
{"mshtml":function(S){return S.style.cssText.toLowerCase();
},
"default":function(S){return S.getAttribute(j);
}}),
COMPUTED_MODE:1,
CASCADED_MODE:2,
LOCAL_MODE:3,
set:function(S,
P,
R,
T){{};
var K=this.__cJ;
P=K.styleNames[P]||P;
if(T!==false&&K.special[P]){switch(P){case f:return qx.bom.element.Clip.set(S,
R);
case d:return qx.bom.element.Cursor.set(S,
R);
case e:return qx.bom.element.Opacity.set(S,
R);
case c:return qx.bom.element.BoxSizing.set(S,
R);
case h:return qx.bom.element.Overflow.setX(S,
R);
case g:return qx.bom.element.Overflow.setY(S,
R);
}}S.style[P]=R!==null?R:a;
},
setStyles:function(S,
U,
T){{};
for(var P in U){this.set(S,
P,
U[P],
T);
}},
reset:function(S,
P,
T){var K=this.__cJ;
P=K.styleNames[P]||P;
if(T!==false&&K.special[P]){switch(P){case f:return qx.bom.element.Clip.reset(S);
case d:return qx.bom.element.Cursor.reset(S);
case e:return qx.bom.element.Opacity.reset(S);
case c:return qx.bom.element.BoxSizing.reset(S);
case h:return qx.bom.element.Overflow.resetX(S);
case g:return qx.bom.element.Overflow.resetY(S);
}}S.style[P]=a;
},
get:qx.core.Variant.select(b,
{"mshtml":function(S,
P,
V,
T){var K=this.__cJ;
P=K.styleNames[P]||P;
if(T!==false&&K.special[P]){switch(P){case f:return qx.bom.element.Clip.get(S,
V);
case d:return qx.bom.element.Cursor.get(S,
V);
case e:return qx.bom.element.Opacity.get(S,
V);
case c:return qx.bom.element.BoxSizing.get(S,
V);
case h:return qx.bom.element.Overflow.getX(S,
V);
case g:return qx.bom.element.Overflow.getY(S,
V);
}}if(!S.currentStyle){return S.style[P]||a;
}switch(V){case this.LOCAL_MODE:return S.style[P]||a;
case this.CASCADED_MODE:return S.currentStyle[P]||a;
default:var W=S.currentStyle[P]||a;
if(/^-?[\.\d]+(px)?$/i.test(W)){return W;
}var X=K.mshtmlPixel[P];
if(X){var Y=S.style[P];
S.style[P]=W||0;
var R=S.style[X]+k;
S.style[P]=Y;
return R;
}if(/^-?[\.\d]+(em|pt|%)?$/i.test(W)){throw new Error("Untranslated computed property value: "+P+". Only pixel values work well across different clients.");
}return W;
}},
"default":function(S,
P,
V,
T){var K=this.__cJ;
P=K.styleNames[P]||P;
if(T!==false&&K.special[P]){switch(P){case f:return qx.bom.element.Clip.get(S,
V);
case d:return qx.bom.element.Cursor.get(S,
V);
case e:return qx.bom.element.Opacity.get(S,
V);
case c:return qx.bom.element.BoxSizing.get(S,
V);
case h:return qx.bom.element.Overflow.getX(S,
V);
case g:return qx.bom.element.Overflow.getY(S,
V);
}}switch(V){case this.LOCAL_MODE:return S.style[P]||a;
case this.CASCADED_MODE:if(S.currentStyle){return S.currentStyle[P]||a;
}throw new Error("Cascaded styles are not supported in this browser!");
default:var ba=qx.dom.Node.getDocument(S);
var bb=ba.defaultView.getComputedStyle(S,
null);
return bb?bb[P]:a;
}}})}});
})();
(function(){var a="auto",
b="px",
c=",",
d="",
e="clip:auto;",
f="rect(",
g=");",
h=")",
i="qx.bom.element.Clip",
j="string",
k="clip:rect(",
l="clip",
m="rect(auto,auto,auto,auto)";
qx.Class.define(i,
{statics:{compile:function(n){if(!n){return e;
}var o=n.left;
var p=n.top;
var q=n.width;
var r=n.height;
var s,
t;
if(o==null){s=(q==null?a:q+b);
o=a;
}else{s=(q==null?a:o+q+b);
o=o+b;
}
if(p==null){t=(r==null?a:r+b);
p=a;
}else{t=(r==null?a:p+r+b);
p=p+b;
}return k+p+c+s+c+t+c+o+g;
},
get:function(u,
v){var w=qx.bom.element.Style.get(u,
l,
v,
false);
var o,
p,
q,
r;
var s,
t;
if(typeof w===j&&w!==a&&w!==d){w=qx.lang.String.trim(w);
if(/\((.*)\)/.test(w)){var x=RegExp.$1.split(c);
p=qx.lang.String.trim(x[0]);
s=qx.lang.String.trim(x[1]);
t=qx.lang.String.trim(x[2]);
o=qx.lang.String.trim(x[3]);
if(o===a){o=null;
}
if(p===a){p=null;
}
if(s===a){s=null;
}
if(t===a){t=null;
}if(p!=null){p=parseInt(p,
10);
}
if(s!=null){s=parseInt(s,
10);
}
if(t!=null){t=parseInt(t,
10);
}
if(o!=null){o=parseInt(o,
10);
}if(s!=null&&o!=null){q=s-o;
}else if(s!=null){q=s;
}
if(t!=null&&p!=null){r=t-p;
}else if(t!=null){r=t;
}}else{throw new Error("Could not parse clip string: "+w);
}}return {left:o||null,
top:p||null,
width:q||null,
height:r||null};
},
set:function(u,
n){if(!n){u.style.clip=m;
return;
}var o=n.left;
var p=n.top;
var q=n.width;
var r=n.height;
var s,
t;
if(o==null){s=(q==null?a:q+b);
o=a;
}else{s=(q==null?a:o+q+b);
o=o+b;
}
if(p==null){t=(r==null?a:r+b);
p=a;
}else{t=(r==null?a:p+r+b);
p=p+b;
}u.style.clip=f+p+c+s+c+t+c+o+h;
},
reset:function(u){u.style.clip=d;
}}});
})();
(function(){var a="n-resize",
b="e-resize",
c="nw-resize",
d="ne-resize",
e="",
f="cursor:",
g="qx.client",
h=";",
i="qx.bom.element.Cursor",
j="cursor",
k="hand";
qx.Class.define(i,
{statics:{__cL:qx.core.Variant.select(g,
{"mshtml":{"cursor":k,
"ew-resize":b,
"ns-resize":a,
"nesw-resize":d,
"nwse-resize":c},
"opera":{"col-resize":b,
"row-resize":a,
"ew-resize":b,
"ns-resize":a,
"nesw-resize":d,
"nwse-resize":c},
"default":{}}),
compile:function(l){return f+(this.__cL[l]||l)+h;
},
get:function(m,
n){return qx.bom.element.Style.get(m,
j,
n,
false);
},
set:function(m,
o){m.style.cursor=this.__cL[o]||o;
},
reset:function(m){m.style.cursor=e;
}}});
})();
(function(){var a="",
b="qx.client",
c=";",
d="filter",
e="opacity:",
f="opacity",
g="MozOpacity",
h=");",
i=")",
j="zoom:1;filter:alpha(opacity=",
k="qx.bom.element.Opacity",
l="alpha(opacity=",
m="-moz-opacity:";
qx.Class.define(k,
{statics:{compile:qx.core.Variant.select(b,
{"mshtml":function(n){if(n>=1){return a;
}
if(n<0.00001){n=0;
}return j+(n*100)+h;
},
"gecko":function(n){if(n==1){n=0.999999;
}
if(qx.bom.client.Engine.VERSION<1.7){return m+n+c;
}else{return e+n+c;
}},
"default":function(n){if(n==1){return a;
}return e+n+c;
}}),
set:qx.core.Variant.select(b,
{"mshtml":function(o,
n){var p=qx.bom.element.Style.get(o,
d,
qx.bom.element.Style.COMPUTED_MODE,
false);
if(n>=1){o.style.filter=p.replace(/alpha\([^\)]*\)/gi,
a);
return;
}
if(n<0.00001){n=0;
}if(!o.currentStyle.hasLayout){o.style.zoom=1;
}o.style.filter=p.replace(/alpha\([^\)]*\)/gi,
a)+l+n*100+i;
},
"gecko":function(o,
n){if(n==1){n=0.999999;
}
if(qx.bom.client.Engine.VERSION<1.7){o.style.MozOpacity=n;
}else{o.style.opacity=n;
}},
"default":function(o,
n){if(n==1){n=a;
}o.style.opacity=n;
}}),
reset:qx.core.Variant.select(b,
{"mshtml":function(o){var p=qx.bom.element.Style.get(o,
d,
qx.bom.element.Style.COMPUTED_MODE,
false);
o.style.filter=p.replace(/alpha\([^\)]*\)/gi,
a);
},
"gecko":function(o){if(qx.bom.client.Engine.VERSION<1.7){o.style.MozOpacity=a;
}else{o.style.opacity=a;
}},
"default":function(o){o.style.opacity=a;
}}),
get:qx.core.Variant.select(b,
{"mshtml":function(o,
q){var p=qx.bom.element.Style.get(o,
d,
q,
false);
if(p){var n=p.match(/alpha\(opacity=(.*)\)/);
if(n&&n[1]){return parseFloat(n[1])/100;
}}return 1.0;
},
"gecko":function(o,
q){var n=qx.bom.element.Style.get(o,
qx.bom.client.Engine.VERSION<1.7?g:f,
q,
false);
if(n==0.999999){n=1.0;
}
if(n!=null){return parseFloat(n);
}return 1.0;
},
"default":function(o,
q){var n=qx.bom.element.Style.get(o,
f,
q,
false);
if(n!=null){return parseFloat(n);
}return 1.0;
}})}});
})();
(function(){var a="qx.client",
b="",
c="boxSizing",
d="box-sizing",
e=":",
f="border-box",
g="qx.bom.element.BoxSizing",
h="KhtmlBoxSizing",
j="-moz-box-sizing",
k="WebkitBoxSizing",
m=";",
n="-khtml-box-sizing",
o="content-box",
p="-webkit-box-sizing",
q="MozBoxSizing";
qx.Class.define(g,
{statics:{__cM:qx.core.Variant.select(a,
{"mshtml":null,
"webkit":[c,
h,
k],
"gecko":[q],
"opera":[c]}),
__cN:qx.core.Variant.select(a,
{"mshtml":null,
"webkit":[d,
n,
p],
"gecko":[j],
"opera":[d]}),
__cO:{tags:{button:true,
select:true},
types:{search:true,
button:true,
submit:true,
reset:true,
checkbox:true,
radio:true}},
__cP:function(r){var s=this.__cO;
return s.tags[r.tagName.toLowerCase()]||s.types[r.type];
},
compile:qx.core.Variant.select(a,
{"mshtml":function(t){{};
},
"default":function(t){var u=this.__cN;
var v=b;
if(u){for(var w=0,
x=u.length;w<x;w++){v+=u[w]+e+t+m;
}}return v;
}}),
get:qx.core.Variant.select(a,
{"mshtml":function(r){if(qx.bom.Document.isStandardMode(qx.dom.Node.getDocument(r))){if(!this.__cP(r)){return o;
}}return f;
},
"default":function(r){var u=this.__cM;
var t;
if(u){for(var w=0,
x=u.length;w<x;w++){t=qx.bom.element.Style.get(r,
u[w],
null,
false);
if(t!=null&&t!==b){return t;
}}}return b;
}}),
set:qx.core.Variant.select(a,
{"mshtml":function(r,
t){{};
},
"default":function(r,
t){var u=this.__cM;
if(u){for(var w=0,
x=u.length;w<x;w++){r.style[u[w]]=t;
}}}}),
reset:function(r){this.set(r,
b);
}}});
})();
(function(){var a="CSS1Compat",
b="qx.bom.Document";
qx.Class.define(b,
{statics:{isQuirksMode:function(c){return (c||window).document.compatMode!==a;
},
isStandardMode:function(c){return (c||window).document.compatMode===a;
},
getWidth:function(c){var d=(c||window).document;
var e=qx.bom.Viewport.getWidth(c);
var f=d.compatMode===a?d.documentElement.scrollWidth:d.body.scrollWidth;
return Math.max(f,
e);
},
getHeight:function(c){var d=(c||window).document;
var e=qx.bom.Viewport.getHeight(c);
var f=d.compatMode===a?d.documentElement.scrollHeight:d.body.scrollHeight;
return Math.max(f,
e);
}}});
})();
(function(){var a="qx.client",
b="CSS1Compat",
c="qx.bom.Viewport";
qx.Class.define(c,
{statics:{getWidth:qx.core.Variant.select(a,
{"opera":function(d){return (d||window).document.body.clientWidth;
},
"webkit":function(d){return (d||window).innerWidth;
},
"default":function(d){var e=(d||window).document;
return e.compatMode===b?e.documentElement.clientWidth:e.body.clientWidth;
}}),
getHeight:qx.core.Variant.select(a,
{"opera":function(d){return (d||window).document.body.clientHeight;
},
"webkit":function(d){return (d||window).innerHeight;
},
"default":function(d){var e=(d||window).document;
return e.compatMode===b?e.documentElement.clientHeight:e.body.clientHeight;
}}),
getScrollLeft:qx.core.Variant.select(a,
{"mshtml":function(d){var e=(d||window).document;
return e.documentElement.scrollLeft||e.body.scrollLeft;
},
"default":function(d){return (d||window).pageXOffset;
}}),
getScrollTop:qx.core.Variant.select(a,
{"mshtml":function(d){var e=(d||window).document;
return e.documentElement.scrollTop||e.body.scrollTop;
},
"default":function(d){return (d||window).pageYOffset;
}})}});
})();
(function(){var a="borderTopWidth",
b="borderLeftWidth",
c="scroll",
d="CSS1Compat",
e="marginTop",
f="marginLeft",
g="border-box",
h="borderBottomWidth",
i="qx.client",
j="borderRightWidth",
k="auto",
l="padding",
m="position",
n="fixed",
o="qx.bom.element.Location",
p="paddingLeft",
q="marginBottom",
r="visible",
s="BODY",
t="paddingBottom",
u="paddingTop",
v="marginRight",
w="margin",
x="overflow",
y="paddingRight",
z="border",
A="absolute";
qx.Class.define(o,
{statics:{__cQ:function(B,
C){return qx.bom.element.Style.get(B,
C,
qx.bom.element.Style.COMPUTED_MODE,
false);
},
__cR:function(B,
C){return parseInt(qx.bom.element.Style.get(B,
C,
qx.bom.element.Style.COMPUTED_MODE,
false),
10)||0;
},
__cS:function(B){var D=0,
E=0;
if(B.getBoundingClientRect){var F=qx.dom.Node.getWindow(B);
D-=qx.bom.Viewport.getScrollLeft(F);
E-=qx.bom.Viewport.getScrollTop(F);
}else{var G=qx.dom.Node.getDocument(B).body;
B=B.parentNode;
while(B&&B!=G){D+=B.scrollLeft;
E+=B.scrollTop;
B=B.parentNode;
}}return {left:D,
top:E};
},
__cT:qx.core.Variant.select(i,
{"mshtml":function(B){var H=qx.dom.Node.getDocument(B);
var G=H.body;
var D=G.offsetLeft;
var E=G.offsetTop;
D-=G.parentNode.clientLeft;
E-=G.parentNode.clientTop;
if(H.compatMode===d){D+=this.__cR(G,
f);
E+=this.__cR(G,
e);
}return {left:D,
top:E};
},
"webkit":function(B){var H=qx.dom.Node.getDocument(B);
var G=H.body;
var D=G.offsetLeft;
var E=G.offsetTop;
D+=this.__cR(G,
b);
E+=this.__cR(G,
a);
if(H.compatMode===d){D+=this.__cR(G,
f);
E+=this.__cR(G,
e);
}return {left:D,
top:E};
},
"gecko":function(B){var G=qx.dom.Node.getDocument(B).body;
var D=G.offsetLeft;
var E=G.offsetTop;
if(qx.bom.element.BoxSizing.get(G)!==g){D+=this.__cR(G,
b);
E+=this.__cR(G,
a);
if(!B.getBoundingClientRect){var I;
while(B){if(this.__cQ(B,
m)===A||this.__cQ(B,
m)===n){I=true;
break;
}B=B.offsetParent;
}
if(!I){D+=this.__cR(G,
b);
E+=this.__cR(G,
a);
}}}return {left:D,
top:E};
},
"default":function(B){var G=qx.dom.Node.getDocument(B).body;
var D=G.offsetLeft;
var E=G.offsetTop;
return {left:D,
top:E};
}}),
__cU:qx.core.Variant.select(i,
{"mshtml|webkit":function(B){var H=qx.dom.Node.getDocument(B);
if(B.getBoundingClientRect){var J=B.getBoundingClientRect();
var D=J.left;
var E=J.top;
if(H.compatMode===d){D-=this.__cR(B,
b);
E-=this.__cR(B,
a);
}}else{var D=B.offsetLeft;
var E=B.offsetTop;
B=B.offsetParent;
var G=H.body;
while(B&&B!=G){D+=B.offsetLeft;
E+=B.offsetTop;
D+=this.__cR(B,
b);
E+=this.__cR(B,
a);
B=B.offsetParent;
}}return {left:D,
top:E};
},
"gecko":function(B){if(B.getBoundingClientRect){var J=B.getBoundingClientRect();
var D=Math.round(J.left);
var E=Math.round(J.top);
}else{var D=0;
var E=0;
var G=qx.dom.Node.getDocument(B).body;
var K=qx.bom.element.BoxSizing;
if(K.get(B)!==g){D-=this.__cR(B,
b);
E-=this.__cR(B,
a);
}
while(B&&B!==G){D+=B.offsetLeft;
E+=B.offsetTop;
if(K.get(B)!==g){D+=this.__cR(B,
b);
E+=this.__cR(B,
a);
}if(B.parentNode&&this.__cQ(B.parentNode,
x)!=r){D+=this.__cR(B.parentNode,
b);
E+=this.__cR(B.parentNode,
a);
}B=B.offsetParent;
}}return {left:D,
top:E};
},
"default":function(B){var D=0;
var E=0;
var G=qx.dom.Node.getDocument(B).body;
while(B&&B!==G){D+=B.offsetLeft;
E+=B.offsetTop;
B=B.offsetParent;
}return {left:D,
top:E};
}}),
get:function(B,
L){var G=this.__cT(B);
if(B.tagName==s){var D=G.left;
var E=G.top;
}else{var M=this.__cU(B);
var N=this.__cS(B);
var D=M.left+G.left-N.left;
var E=M.top+G.top-N.top;
}var O=D+B.offsetWidth;
var P=E+B.offsetHeight;
if(L){if(L==l||L==c){var Q=qx.bom.element.Overflow.getX(B);
if(Q==c||Q==k){O+=B.scrollWidth-B.offsetWidth+this.__cR(B,
b)+this.__cR(B,
j);
}var R=qx.bom.element.Overflow.getY(B);
if(R==c||R==k){P+=B.scrollHeight-B.offsetHeight+this.__cR(B,
a)+this.__cR(B,
h);
}}
switch(L){case l:D+=this.__cR(B,
p);
E+=this.__cR(B,
u);
O-=this.__cR(B,
y);
P-=this.__cR(B,
t);
case c:D-=B.scrollLeft;
E-=B.scrollTop;
O-=B.scrollLeft;
P-=B.scrollTop;
case z:D+=this.__cR(B,
b);
E+=this.__cR(B,
a);
O-=this.__cR(B,
j);
P-=this.__cR(B,
h);
break;
case w:D-=this.__cR(B,
f);
E-=this.__cR(B,
e);
O+=this.__cR(B,
v);
P+=this.__cR(B,
q);
break;
}}return {left:D,
top:E,
right:O,
bottom:P};
},
getLeft:function(B,
L){return this.get(B,
L).left;
},
getTop:function(B,
L){return this.get(B,
L).top;
},
getRight:function(B,
L){return this.get(B,
L).right;
},
getBottom:function(B,
L){return this.get(B,
L).bottom;
},
getRelative:function(S,
T,
U,
V){var W=this.get(S,
U);
var X=this.get(T,
V);
return {left:W.left-X.left,
top:W.top-X.top,
right:W.right-X.right,
bottom:W.bottom-X.bottom};
}}});
})();
(function(){var a="qx.event.handler.Appear",
b="__cW",
c="__cV",
d="disappear",
e="appear";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(f){arguments.callee.base.call(this);
this.__cV=f;
this.__cW={};
qx.event.handler.Appear.__cX[this.$$hash]=this;
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{appear:true,
disappear:true},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:true,
__cX:{},
refresh:function(){var g=this.__cX;
for(var h in g){g[h].refresh();
}}},
members:{canHandleEvent:function(i,
j){},
registerEvent:function(i,
j,
k){var h=qx.core.ObjectRegistry.toHashCode(i);
var l=this.__cW;
if(l&&!l[h]){l[h]=i;
i.$$displayed=i.offsetWidth>0;
}},
unregisterEvent:function(i,
j,
k){var h=qx.core.ObjectRegistry.toHashCode(i);
var l=this.__cW;
if(!l){return;
}
if(l[h]){delete l[h];
i.$$displayed=null;
}},
refresh:function(){var l=this.__cW;
var m;
for(var h in l){m=l[h];
var n=m.offsetWidth>0;
if((!!m.$$displayed)!==n){m.$$displayed=n;
var o=qx.event.Registration.createEvent(n?e:d);
this.__cV.dispatchEvent(m,
o);
}}}},
destruct:function(){this._disposeFields(c,
b);
delete qx.event.handler.Appear.__cX[this.$$hash];
},
defer:function(p){qx.event.Registration.addHandler(p);
}});
})();
(function(){var a="abstract",
b="qx.event.dispatch.AbstractBubbling";
qx.Class.define(b,
{extend:qx.core.Object,
implement:qx.event.IEventDispatcher,
type:a,
construct:function(c){this._manager=c;
},
members:{_getParent:function(d){throw new Error("Missing implementation");
},
canDispatchEvent:function(d,
e,
f){return e.getBubbles();
},
dispatchEvent:function(d,
e,
f){var g=d;
var c=this._manager;
var h,
k;
var l;
var m,
n;
var o;
var p=[];
h=c.getListeners(d,
f,
true);
k=c.getListeners(d,
f,
false);
if(h){p.push(h);
}
if(k){p.push(k);
}var g=this._getParent(d);
var q=[];
var r=[];
var s=[];
var t=[];
while(g!=null){h=c.getListeners(g,
f,
true);
if(h){s.push(h);
t.push(g);
}k=c.getListeners(g,
f,
false);
if(k){q.push(k);
r.push(g);
}g=this._getParent(g);
}e.setEventPhase(qx.event.type.Event.CAPTURING_PHASE);
for(var u=s.length-1;u>=0;u--){o=t[u];
e.setCurrentTarget(o);
l=s[u];
for(var v=0,
w=l.length;v<w;v++){m=l[v];
n=m.context||o;
m.handler.call(n,
e);
}
if(e.getPropagationStopped()){return;
}}e.setEventPhase(qx.event.type.Event.AT_TARGET);
e.setCurrentTarget(d);
for(var u=0,
x=p.length;u<x;u++){l=p[u];
for(var v=0,
w=l.length;v<w;v++){m=l[v];
n=m.context||d;
m.handler.call(n,
e);
}
if(e.getPropagationStopped()){return;
}}e.setEventPhase(qx.event.type.Event.BUBBLING_PHASE);
for(var u=0,
x=q.length;u<x;u++){o=r[u];
e.setCurrentTarget(o);
l=q[u];
for(var v=0,
w=l.length;v<w;v++){m=l[v];
n=m.context||o;
m.handler.call(n,
e);
}
if(e.getPropagationStopped()){return;
}}}}});
})();
(function(){var a="qx.event.dispatch.DomBubbling";
qx.Class.define(a,
{extend:qx.event.dispatch.AbstractBubbling,
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL},
members:{_getParent:function(b){return b.parentNode;
},
canDispatchEvent:function(b,
c,
d){return b.nodeType!==undefined&&c.getBubbles();
}},
defer:function(e){qx.event.Registration.addDispatcher(e);
}});
})();
(function(){var a="keydown",
b="qx.client",
c="keypress",
d="NumLock",
e="keyup",
f="Enter",
g="0",
h="9",
i="-",
j="PageUp",
k="+",
l="PrintScreen",
m="gecko",
n="A",
o="Left",
p="F5",
q="Down",
r="Up",
s="F11",
t="F6",
u="useraction",
v="keyinput",
w="Insert",
x="F8",
y="End",
z="/",
A="Delete",
B="*",
C="F1",
D="F4",
E="Home",
F="F2",
G="F12",
H="PageDown",
I="F7",
J="F9",
K="F10",
L="Right",
M="F3",
N="Z",
O="Escape",
P="webkit",
Q="__cY",
R="__dc",
S="Space",
T="5",
U="3",
V="Meta",
W="7",
X="CapsLock",
Y="Scroll",
ba="Control",
bb="Tab",
bc="Shift",
bd="Pause",
be="Unidentified",
bf="qx.event.handler.Keyboard",
bg="mshtml|webkit",
bh="__da",
bi="6",
bj="__db",
bk="Apps",
bl="4",
bm="Alt",
bn="2",
bo="mshtml",
bp="1",
bq="8",
br="Win",
bs=",",
bt="Backspace";
qx.Class.define(bf,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(bu){arguments.callee.base.call(this);
this.__cY=bu;
this.__da=bu.getWindow();
if(qx.core.Variant.isSet(b,
m)){this.__db=this.__da;
}else{this.__db=this.__da.document.documentElement;
}this.__dc={};
this._initKeyObserver();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{keyup:1,
keydown:1,
keypress:1,
keyinput:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:true,
isValidKeyIdentifier:function(bv){if(this._identifierToKeyCodeMap[bv]){return true;
}
if(bv.length!=1){return false;
}
if(bv>=g&&bv<=h){return true;
}
if(bv>=n&&bv<=N){return true;
}
switch(bv){case k:case i:case B:case z:return true;
default:return false;
}}},
members:{__dd:null,
__cY:null,
__da:null,
__db:null,
__dc:null,
canHandleEvent:function(bw,
bx){},
registerEvent:function(bw,
bx,
by){},
unregisterEvent:function(bw,
bx,
by){},
_fireInputEvent:function(bz,
bA){var bB=this.__cY.getHandler(qx.event.handler.Focus);
var bw=bB.getActive();
if(!bw||bw.offsetWidth==0){bw=bB.getFocus();
}if(bw&&bw.offsetWidth!=0){var bC=qx.event.Registration.createEvent(v,
qx.event.type.KeyInput,
[bz,
bw,
bA]);
this.__cY.dispatchEvent(bw,
bC);
}if(this.__da){qx.event.Registration.fireEvent(this.__da,
u,
qx.event.type.Data,
[v]);
}},
_fireSequenceEvent:function(bz,
bx,
bv){var bB=this.__cY.getHandler(qx.event.handler.Focus);
var bw=bB.getActive();
if(!bw||bw.offsetWidth==0){bw=bB.getFocus();
}if(!bw||bw.offsetWidth==0){bw=this.__cY.getWindow().document.body;
}var bC=qx.event.Registration.createEvent(bx,
qx.event.type.KeySequence,
[bz,
bw,
bv]);
this.__cY.dispatchEvent(bw,
bC);
if(qx.core.Variant.isSet(b,
bg)){if(bx==a&&bC.getDefaultPrevented()){var bD=bz.keyCode;
if(!(this._isNonPrintableKeyCode(bD)||bD==8||bD==9)){this._fireSequenceEvent(bz,
c,
bv);
}}}if(this.__da){qx.event.Registration.fireEvent(this.__da,
u,
qx.event.type.Data,
[bx]);
}},
_initKeyObserver:function(){this.__dd=qx.lang.Function.listener(this._onKeyUpDown,
this);
this._onKeyPressWrapper=qx.lang.Function.listener(this._onKeyPress,
this);
var bE=qx.bom.Event;
bE.addNativeListener(this.__db,
e,
this.__dd);
bE.addNativeListener(this.__db,
a,
this.__dd);
bE.addNativeListener(this.__db,
c,
this._onKeyPressWrapper);
},
_stopKeyObserver:function(){var bE=qx.bom.Event;
bE.removeNativeListener(this.__db,
e,
this.__dd);
bE.removeNativeListener(this.__db,
a,
this.__dd);
bE.removeNativeListener(this.__db,
c,
this._onKeyPressWrapper);
},
_onKeyUpDown:qx.core.Variant.select(b,
{"mshtml":function(bz){bz=window.event||bz;
var bD=bz.keyCode;
var bA=0;
var bx=bz.type;
if(!(this.__dc[bD]==a&&bx==a)){this._idealKeyHandler(bD,
bA,
bx,
bz);
}if(bx==a){if(this._isNonPrintableKeyCode(bD)||bD==8||bD==9){this._idealKeyHandler(bD,
bA,
c,
bz);
}}this.__dc[bD]=bx;
},
"gecko":function(bz){var bD=this._keyCodeFix[bz.keyCode]||bz.keyCode;
var bA=bz.charCode;
var bx=bz.type;
if(qx.bom.client.Platform.WIN){var bv=bD?this._keyCodeToIdentifier(bD):this._charCodeToIdentifier(bA);
if(!(this.__dc[bv]==a&&bx==a)){this._idealKeyHandler(bD,
bA,
bx,
bz);
}this.__dc[bv]=bx;
}else{this._idealKeyHandler(bD,
bA,
bx,
bz);
}},
"webkit":function(bz){var bD=0;
var bA=0;
var bx=bz.type;
if(qx.bom.client.Engine.VERSION<525.13){if(bx==e||bx==a){bD=this._charCode2KeyCode[bz.charCode]||bz.keyCode;
}else{if(this._charCode2KeyCode[bz.charCode]){bD=this._charCode2KeyCode[bz.charCode];
}else{bA=bz.charCode;
}}this._idealKeyHandler(bD,
bA,
bx,
bz);
}else{bD=bz.keyCode;
if(!(this.__dc[bD]==a&&bx==a)){this._idealKeyHandler(bD,
bA,
bx,
bz);
}if(bx==a){if(this._isNonPrintableKeyCode(bD)||bD==8||bD==9){this._idealKeyHandler(bD,
bA,
c,
bz);
}}this.__dc[bD]=bx;
}},
"opera":function(bz){this._idealKeyHandler(bz.keyCode,
0,
bz.type,
bz);
}}),
_onKeyPress:qx.core.Variant.select(b,
{"mshtml":function(bz){bz=window.event||bz;
if(this._charCode2KeyCode[bz.keyCode]){this._idealKeyHandler(this._charCode2KeyCode[bz.keyCode],
0,
bz.type,
bz);
}else{this._idealKeyHandler(0,
bz.keyCode,
bz.type,
bz);
}},
"gecko":function(bz){var bD=this._keyCodeFix[bz.keyCode]||bz.keyCode;
var bA=bz.charCode;
var bx=bz.type;
this._idealKeyHandler(bD,
bA,
bx,
bz);
},
"webkit":function(bz){if(qx.bom.client.Engine.VERSION<525.13){var bD=0;
var bA=0;
var bx=bz.type;
if(bx==e||bx==a){bD=this._charCode2KeyCode[bz.charCode]||bz.keyCode;
}else{if(this._charCode2KeyCode[bz.charCode]){bD=this._charCode2KeyCode[bz.charCode];
}else{bA=bz.charCode;
}}this._idealKeyHandler(bD,
bA,
bx,
bz);
}else{if(this._charCode2KeyCode[bz.keyCode]){this._idealKeyHandler(this._charCode2KeyCode[bz.keyCode],
0,
bz.type,
bz);
}else{this._idealKeyHandler(0,
bz.keyCode,
bz.type,
bz);
}}},
"opera":function(bz){if(this._keyCodeToIdentifierMap[bz.keyCode]){this._idealKeyHandler(bz.keyCode,
0,
bz.type,
bz);
}else{this._idealKeyHandler(0,
bz.keyCode,
bz.type,
bz);
}}}),
_idealKeyHandler:function(bD,
bA,
bF,
bz){if(!bD&&!bA){return;
}var bv;
if(bD){bv=this._keyCodeToIdentifier(bD);
this._fireSequenceEvent(bz,
bF,
bv);
}else{bv=this._charCodeToIdentifier(bA);
this._fireSequenceEvent(bz,
c,
bv);
this._fireInputEvent(bz,
bA);
}},
_specialCharCodeMap:{8:bt,
9:bb,
13:f,
27:O,
32:S},
_keyCodeToIdentifierMap:{16:bc,
17:ba,
18:bm,
20:X,
224:V,
37:o,
38:r,
39:L,
40:q,
33:j,
34:H,
35:y,
36:E,
45:w,
46:A,
112:C,
113:F,
114:M,
115:D,
116:p,
117:t,
118:I,
119:x,
120:J,
121:K,
122:s,
123:G,
144:d,
44:l,
145:Y,
19:bd,
91:br,
93:bk},
_numpadToCharCode:{96:g.charCodeAt(0),
97:bp.charCodeAt(0),
98:bn.charCodeAt(0),
99:U.charCodeAt(0),
100:bl.charCodeAt(0),
101:T.charCodeAt(0),
102:bi.charCodeAt(0),
103:W.charCodeAt(0),
104:bq.charCodeAt(0),
105:h.charCodeAt(0),
106:B.charCodeAt(0),
107:k.charCodeAt(0),
109:i.charCodeAt(0),
110:bs.charCodeAt(0),
111:z.charCodeAt(0)},
_charCodeA:n.charCodeAt(0),
_charCodeZ:N.charCodeAt(0),
_charCode0:g.charCodeAt(0),
_charCode9:h.charCodeAt(0),
_isNonPrintableKeyCode:function(bD){return this._keyCodeToIdentifierMap[bD]?true:false;
},
_isIdentifiableKeyCode:function(bD){if(bD>=this._charCodeA&&bD<=this._charCodeZ){return true;
}if(bD>=this._charCode0&&bD<=this._charCode9){return true;
}if(this._specialCharCodeMap[bD]){return true;
}if(this._numpadToCharCode[bD]){return true;
}if(this._isNonPrintableKeyCode(bD)){return true;
}return false;
},
_keyCodeToIdentifier:function(bD){if(this._isIdentifiableKeyCode(bD)){var bG=this._numpadToCharCode[bD];
if(bG){return String.fromCharCode(bG);
}return (this._keyCodeToIdentifierMap[bD]||this._specialCharCodeMap[bD]||String.fromCharCode(bD));
}else{return be;
}},
_charCodeToIdentifier:function(bA){return this._specialCharCodeMap[bA]||String.fromCharCode(bA).toUpperCase();
},
_identifierToKeyCode:function(bv){return qx.event.handler.Keyboard._identifierToKeyCodeMap[bv]||bv.charCodeAt(0);
}},
destruct:function(){this._stopKeyObserver();
this._disposeFields(Q,
bh,
bj,
R);
},
defer:function(bH,
bI,
bJ){qx.event.Registration.addHandler(bH);
if(!bH._identifierToKeyCodeMap){bH._identifierToKeyCodeMap={};
for(var bK in bI._keyCodeToIdentifierMap){bH._identifierToKeyCodeMap[bI._keyCodeToIdentifierMap[bK]]=parseInt(bK,
10);
}
for(var bK in bI._specialCharCodeMap){bH._identifierToKeyCodeMap[bI._specialCharCodeMap[bK]]=parseInt(bK,
10);
}}
if(qx.core.Variant.isSet(b,
bo)){bI._charCode2KeyCode={13:13,
27:27};
}else if(qx.core.Variant.isSet(b,
m)){bI._keyCodeFix={12:bI._identifierToKeyCode(d)};
}else if(qx.core.Variant.isSet(b,
P)){if(qx.bom.client.Engine.VERSION<525.13){bI._charCode2KeyCode={63289:bI._identifierToKeyCode(d),
63276:bI._identifierToKeyCode(j),
63277:bI._identifierToKeyCode(H),
63275:bI._identifierToKeyCode(y),
63273:bI._identifierToKeyCode(E),
63234:bI._identifierToKeyCode(o),
63232:bI._identifierToKeyCode(r),
63235:bI._identifierToKeyCode(L),
63233:bI._identifierToKeyCode(q),
63272:bI._identifierToKeyCode(A),
63302:bI._identifierToKeyCode(w),
63236:bI._identifierToKeyCode(C),
63237:bI._identifierToKeyCode(F),
63238:bI._identifierToKeyCode(M),
63239:bI._identifierToKeyCode(D),
63240:bI._identifierToKeyCode(p),
63241:bI._identifierToKeyCode(t),
63242:bI._identifierToKeyCode(I),
63243:bI._identifierToKeyCode(x),
63244:bI._identifierToKeyCode(J),
63245:bI._identifierToKeyCode(K),
63246:bI._identifierToKeyCode(s),
63247:bI._identifierToKeyCode(G),
63248:bI._identifierToKeyCode(l),
3:bI._identifierToKeyCode(f),
12:bI._identifierToKeyCode(d),
13:bI._identifierToKeyCode(f)};
}else{bI._charCode2KeyCode={13:13,
27:27};
}}}});
})();
(function(){var a="qx.client",
b="mouseup",
c="mousedown",
d="click",
e="contextmenu",
f="dblclick",
g="mousewheel",
h="mouseover",
i="mouseout",
j="DOMMouseScroll",
k="mousemove",
l="mshtml|webkit|opera",
m="useraction",
n="__dl",
o="__df",
p="__de",
q="__dg",
r="qx.event.handler.Mouse",
s="gecko|webkit";
qx.Class.define(r,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(t){arguments.callee.base.call(this);
this.__de=t;
this.__df=t.getWindow();
this.__dg=this.__df.document.documentElement;
this._initButtonObserver();
this._initMoveObserver();
this._initWheelObserver();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{mousemove:1,
mouseover:1,
mouseout:1,
mousedown:1,
mouseup:1,
click:1,
dblclick:1,
contextmenu:1,
mousewheel:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:true},
members:{__dh:null,
__di:null,
__dj:null,
__dk:null,
__dl:null,
__de:null,
__df:null,
__dg:null,
canHandleEvent:function(u,
v){},
registerEvent:function(u,
v,
w){},
unregisterEvent:function(u,
v,
w){},
__dm:function(x,
v,
u){if(!u){u=x.target||x.srcElement;
}qx.event.Registration.fireEvent(u,
v||x.type,
qx.event.type.Mouse,
[x,
u,
null,
true,
true]);
qx.event.Registration.fireEvent(this.__df,
m,
qx.event.type.Data,
[v||x.type]);
},
_initButtonObserver:function(){this.__dh=qx.lang.Function.listener(this._onButtonEvent,
this);
var y=qx.bom.Event;
y.addNativeListener(this.__dg,
c,
this.__dh);
y.addNativeListener(this.__dg,
b,
this.__dh);
y.addNativeListener(this.__dg,
d,
this.__dh);
y.addNativeListener(this.__dg,
f,
this.__dh);
y.addNativeListener(this.__dg,
e,
this.__dh);
},
_initMoveObserver:function(){this.__di=qx.lang.Function.listener(this._onMoveEvent,
this);
var y=qx.bom.Event;
y.addNativeListener(this.__dg,
k,
this.__di);
y.addNativeListener(this.__dg,
h,
this.__di);
y.addNativeListener(this.__dg,
i,
this.__di);
},
_initWheelObserver:function(){this.__dj=qx.lang.Function.listener(this._onWheelEvent,
this);
var y=qx.bom.Event;
var v=qx.core.Variant.isSet(a,
l)?g:j;
y.addNativeListener(this.__dg,
v,
this.__dj);
},
_stopButtonObserver:function(){var y=qx.bom.Event;
y.removeNativeListener(this.__dg,
c,
this.__dh);
y.removeNativeListener(this.__dg,
b,
this.__dh);
y.removeNativeListener(this.__dg,
d,
this.__dh);
y.removeNativeListener(this.__dg,
f,
this.__dh);
y.removeNativeListener(this.__dg,
e,
this.__dh);
},
_stopMoveObserver:function(){var y=qx.bom.Event;
y.removeNativeListener(this.__dg,
k,
this.__di);
y.removeNativeListener(this.__dg,
h,
this.__di);
y.removeNativeListener(this.__dg,
i,
this.__di);
},
_stopWheelObserver:function(){var y=qx.bom.Event;
var v=qx.core.Variant.isSet(a,
l)?g:j;
y.removeNativeListener(this.__dg,
v,
this.__dj);
},
_onMoveEvent:function(x){this.__dm(x);
},
_onButtonEvent:function(x){var v=x.type;
var u=x.target||x.srcElement;
if(qx.core.Variant.isSet(a,
s)){if(u&&u.nodeType==3){u=u.parentNode;
}}
if(this.__dn){this.__dn(x,
v,
u);
}
if(this.__dp){this.__dp(x,
v,
u);
}this.__dm(x,
v,
u);
if(this.__do){this.__do(x,
v,
u);
}
if(this.__dq){this.__dq(x,
v,
u);
}this.__dk=v;
},
_onWheelEvent:function(x){this.__dm(x,
g);
},
__dn:qx.core.Variant.select(a,
{"webkit":function(x,
v,
u){if(v==e){this.__dm(x,
c,
u);
this.__dm(x,
b,
u);
}},
"default":null}),
__do:qx.core.Variant.select(a,
{"opera":function(x,
v,
u){if(v==b&&x.button==2){this.__dm(x,
e,
u);
}},
"default":null}),
__dp:qx.core.Variant.select(a,
{"mshtml":function(x,
v,
u){if(v==b&&this.__dk==d){this.__dm(x,
c,
u);
}else if(v==f){this.__dm(x,
d,
u);
}},
"default":null}),
__dq:qx.core.Variant.select(a,
{"mshtml":null,
"default":function(x,
v,
u){switch(v){case c:this.__dl=u;
break;
case b:if(u!==this.__dl){var z=qx.dom.Hierarchy.getCommonParent(u,
this.__dl);
this.__dm(x,
d,
z);
}}}})},
destruct:function(){this._stopButtonObserver();
this._stopMoveObserver();
this._stopWheelObserver();
this._disposeFields(p,
o,
q,
n);
},
defer:function(A){qx.event.Registration.addHandler(A);
}});
})();
(function(){var a="qx.event.handler.Capture";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{capture:true,
losecapture:true},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:true},
members:{canHandleEvent:function(b,
c){},
registerEvent:function(b,
c,
d){},
unregisterEvent:function(b,
c,
d){}},
defer:function(e){qx.event.Registration.addHandler(e);
}});
})();
(function(){var a="alias",
b="copy",
c="blur",
d="mousedown",
f="mouseout",
g="keydown",
h="Ctrl",
i="Shift",
j="mousemove",
k="move",
l="mouseover",
m="Alt",
n="keyup",
o="mouseup",
p="dragend",
q="on",
r="qxDraggable",
s="drag",
t="drop",
u="qxDroppable",
v="qx.event.handler.DragDrop",
w="__dF",
x="__dw",
y="droprequest",
z="__dJ",
A="dragstart",
B="__dx",
C="dragchange",
D="__dy",
E="dragleave",
F="__dr",
G="__ds",
H="dragover",
I="__dv";
qx.Class.define(v,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(J){arguments.callee.base.call(this);
this.__dr=J;
this.__ds=J.getWindow().document.documentElement;
this.__dr.addListener(this.__ds,
d,
this._onMouseDown,
this);
this.__du();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{dragstart:1,
dragend:1,
dragover:1,
dragleave:1,
drop:1,
drag:1,
dragchange:1,
droprequest:1},
IGNORE_CAN_HANDLE:true},
members:{canHandleEvent:function(K,
L){},
registerEvent:function(K,
L,
M){},
unregisterEvent:function(K,
L,
M){},
addType:function(L){this.__dv[L]=true;
},
addAction:function(N){this.__dw[N]=true;
},
supportsType:function(L){return !!this.__dv[L];
},
supportsAction:function(L){return !!this.__dw[L];
},
getData:function(L){if(!this.__dI||!this.__dJ){throw new Error("This method must not be used outside the drop event listener!");
}
if(!this.__dv[L]){throw new Error("Unsupported data type: "+L+"!");
}
if(!this.__dy[L]){this.__dt=L;
this.__dB(y,
this.__dF,
false);
}
if(!this.__dy[L]){throw new Error("Please use a dragrequest listener to the drag target to fill the manager with data!");
}return this.__dy[L]||null;
},
getCurrentAction:function(){return this.__dA;
},
addData:function(L,
O){this.__dy[L]=O;
},
getCurrentType:function(){return this.__dt;
},
__du:function(){this.__dv={};
this.__dw={};
this.__dx={};
this.__dy={};
},
__dz:function(){var P=this.__dw;
var Q=this.__dx;
var R=null;
if(this.__dI){if(Q.Shift&&Q.Ctrl&&P.alias){R=a;
}else if(Q.Shift&&Q.Alt&&P.copy){R=b;
}else if(Q.Shift&&P.move){R=k;
}else if(Q.Alt&&P.alias){R=a;
}else if(Q.Ctrl&&P.copy){R=b;
}else if(P.move){R=k;
}else if(P.copy){R=b;
}else if(P.alias){R=a;
}}
if(R!=this.__dA){this.__dA=R;
this.__dB(C,
this.__dF,
false);
}},
__dB:function(L,
K,
S,
T){var U=qx.event.Registration;
var V=U.createEvent(L,
qx.event.type.Drag,
[S,
T]);
if(this.__dF!==this.__dJ){if(K==this.__dF){V.setRelatedTarget(this.__dJ);
}else{V.setRelatedTarget(this.__dF);
}}return U.dispatchEvent(K,
V);
},
__dC:function(W){while(W&&W.nodeType==1){if(W.getAttribute(r)==q){return W;
}W=W.parentNode;
}return null;
},
__dD:function(W){while(W&&W.nodeType==1){if(W.getAttribute(u)==q){return W;
}W=W.parentNode;
}return null;
},
__dE:function(){this.__dF=null;
this.__dr.removeListener(this.__ds,
j,
this._onMouseMove,
this,
true);
this.__dr.removeListener(this.__ds,
o,
this._onMouseUp,
this,
true);
qx.event.Registration.removeListener(window,
c,
this._onWindowBlur,
this);
this.__du();
},
__dG:function(){if(this.__dH){this.__dr.removeListener(this.__ds,
l,
this._onMouseOver,
this,
true);
this.__dr.removeListener(this.__ds,
f,
this._onMouseOut,
this,
true);
this.__dr.removeListener(this.__ds,
g,
this._onKeyDown,
this,
true);
this.__dr.removeListener(this.__ds,
n,
this._onKeyUp,
this,
true);
this.__dB(p,
this.__dF,
false);
this.__dH=false;
}this.__dI=false;
this.__dJ=null;
this.__dE();
},
__dI:false,
_onWindowBlur:function(X){this.__dG();
},
_onKeyDown:function(X){var Y=X.getKeyIdentifier();
switch(Y){case m:case h:case i:if(!this.__dx[Y]){this.__dx[Y]=true;
this.__dz();
}}},
_onKeyUp:function(X){var Y=X.getKeyIdentifier();
switch(Y){case m:case h:case i:if(this.__dx[Y]){this.__dx[Y]=false;
this.__dz();
}}},
_onMouseDown:function(X){if(this.__dH){return;
}var ba=this.__dC(X.getTarget());
if(ba){this.__dK=X.getDocumentLeft();
this.__dL=X.getDocumentTop();
this.__dF=ba;
this.__dr.addListener(this.__ds,
j,
this._onMouseMove,
this,
true);
this.__dr.addListener(this.__ds,
o,
this._onMouseUp,
this,
true);
qx.event.Registration.addListener(window,
c,
this._onWindowBlur,
this);
}},
_onMouseUp:function(X){if(this.__dI){this.__dB(t,
this.__dJ,
false,
X);
}if(this.__dH){X.stopPropagation();
}this.__dG();
},
_onMouseMove:function(X){if(this.__dH){if(!this.__dB(s,
this.__dF,
true,
X)){this.__dG();
}}else{if(Math.abs(X.getDocumentLeft()-this.__dK)>3||Math.abs(X.getDocumentTop()-this.__dL)>3){if(this.__dB(A,
this.__dF,
true,
X)){this.__dH=true;
this.__dr.addListener(this.__ds,
l,
this._onMouseOver,
this,
true);
this.__dr.addListener(this.__ds,
f,
this._onMouseOut,
this,
true);
this.__dr.addListener(this.__ds,
g,
this._onKeyDown,
this,
true);
this.__dr.addListener(this.__ds,
n,
this._onKeyUp,
this,
true);
var Q=this.__dx;
Q.Ctrl=X.isCtrlPressed();
Q.Shift=X.isShiftPressed();
Q.Alt=X.isAltPressed();
this.__dz();
}else{this.__dB(p,
this.__dF,
false);
this.__dE();
}}}},
_onMouseOver:function(X){var K=X.getTarget();
var bb=this.__dD(K);
if(bb&&bb!=this.__dJ){this.__dI=this.__dB(H,
bb,
true,
X);
this.__dJ=bb;
this.__dz();
}},
_onMouseOut:function(X){var K=X.getTarget();
var bb=this.__dD(K);
if(bb&&bb==this.__dJ){this.__dB(E,
this.__dJ,
false,
X);
this.__dJ=null;
this.__dI=false;
this.__dz();
}}},
destruct:function(){this.__dG();
this.__dr.removeListener(this.__ds,
d,
this._onMouseDown,
this);
this._disposeFields(w,
z,
F,
G,
I,
x,
B,
D);
},
defer:function(bc){qx.event.Registration.addHandler(bc);
}});
})();
(function(){var a="-",
b="qx.event.handler.Element",
c="_manager",
d="_registeredEvents";
qx.Class.define(b,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(e){arguments.callee.base.call(this);
this._manager=e;
this._registeredEvents={};
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{abort:true,
scroll:true,
select:true,
reset:true,
submit:true},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:true},
members:{canHandleEvent:function(f,
g){},
registerEvent:function(f,
g,
h){var i=qx.core.ObjectRegistry.toHashCode(f);
var j=i+a+g;
var k=qx.lang.Function.listener(this._onNative,
this,
j);
qx.bom.Event.addNativeListener(f,
g,
k);
this._registeredEvents[j]={element:f,
type:g,
listener:k};
},
unregisterEvent:function(f,
g,
h){var l=this._registeredEvents;
if(!l){return;
}var i=qx.core.ObjectRegistry.toHashCode(f);
var j=i+a+g;
var m=this._registeredEvents[j];
qx.bom.Event.removeNativeListener(f,
g,
m.listener);
delete this._registeredEvents[j];
},
_onNative:function(n,
j){var l=this._registeredEvents;
if(!l){return;
}var m=l[j];
qx.event.Registration.fireNonBubblingEvent(m.element,
m.type,
qx.event.type.Native,
[n]);
}},
destruct:function(){var o;
var l=this._registeredEvents;
for(var p in l){o=l[p];
qx.bom.Event.removeNativeListener(o.element,
o.type,
o.listener);
}this._disposeFields(c,
d);
},
defer:function(q){qx.event.Registration.addHandler(q);
}});
})();
(function(){var a="",
b=">",
c="<",
d=" ",
e="='",
f="http://www.w3.org/1999/xhtml",
g="qx.bom.Element",
h="div",
i="' ",
j="></";
qx.Class.define(g,
{statics:{__dM:{"onload":true,
"onpropertychange":true,
"oninput":true,
"onchange":true,
"name":true,
"type":true,
"checked":true,
"disabled":true},
create:function(k,
l,
m,
n){if(!m){m=window;
}
if(!k){throw new Error("The tag name is missing!");
}var o=this.__dM;
var p=a;
for(var q in l){if(o[q]){p+=q+e+l[q]+i;
}}var r;
if(p!=a){if(qx.bom.client.Engine.MSHTML){r=m.document.createElement(c+k+d+p+b);
}else{var s=m.document.createElement(h);
s.innerHTML=c+k+d+p+j+k+b;
r=s.firstChild;
}}else{if(m.document.createElementNS){r=m.document.createElementNS(f,
k);
}else{r=m.document.createElement(k);
}}
for(var q in l){if(!o[q]){qx.bom.element.Attribute.set(r,
q,
l[q]);
}}return r;
},
empty:function(r){return r.innerHTML=a;
},
addListener:function(r,
t,
u,
v,
w){return qx.event.Registration.addListener(r,
t,
u,
v,
w);
},
removeListener:function(r,
t,
u,
v,
w){return qx.event.Registration.removeListener(r,
t,
u,
v,
w);
},
hasListener:function(r,
t,
w){return qx.event.Registration.hasListener(r,
t,
w);
},
focus:function(r){qx.event.Registration.getManager(r).getHandler(qx.event.handler.Focus).focus(r);
},
blur:function(r){qx.event.Registration.getManager(r).getHandler(qx.event.handler.Focus).blur(r);
},
activate:function(r){qx.event.Registration.getManager(r).getHandler(qx.event.handler.Focus).activate(r);
},
deactivate:function(r){qx.event.Registration.getManager(r).getHandler(qx.event.handler.Focus).deactivate(r);
},
capture:function(r){qx.event.Registration.getManager(r).getDispatcher(qx.event.dispatch.MouseCapture).activateCapture(r);
},
releaseCapture:function(r){qx.event.Registration.getManager(r).getDispatcher(qx.event.dispatch.MouseCapture).releaseCapture(r);
}}});
})();
(function(){var a="qx.client",
b="blur",
c="focus",
d="mousedown",
f="on",
g="mouseup",
h="DOMFocusOut",
i="DOMFocusIn",
j="selectstart",
k="onmousedown",
l="onfocusout",
m="onfocusin",
n="onmouseup",
o="onselectstart",
p="draggesture",
q="_document",
r="_root",
s="qx.event.handler.Focus",
t="_applyFocus",
u="_window",
v="deactivate",
w="_applyActive",
x="focusin",
y="",
z="qxSelectable",
A="tabIndex",
B="off",
C="_body",
D="activate",
E="focusout",
F="__mouseActive",
G="_manager",
H="qxKeepFocus",
I="qxKeepActive";
qx.Class.define(s,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(J){arguments.callee.base.call(this);
this._manager=J;
this._window=J.getWindow();
this._document=this._window.document;
this._root=this._document.documentElement;
this._body=this._document.body;
this._initObserver();
},
properties:{active:{apply:w,
nullable:true},
focus:{apply:t,
nullable:true}},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{focus:1,
blur:1,
focusin:1,
focusout:1,
activate:1,
deactivate:1},
IGNORE_CAN_HANDLE:true,
FOCUSABLE_ELEMENTS:qx.core.Variant.select("qx.client",
{"mshtml|gecko":{a:1,
body:1,
button:1,
frame:1,
iframe:1,
img:1,
input:1,
object:1,
select:1,
textarea:1},
"opera|webkit":{button:1,
input:1,
select:1,
textarea:1}})},
members:{canHandleEvent:function(K,
L){},
registerEvent:function(K,
L,
M){},
unregisterEvent:function(K,
L,
M){},
focus:function(N){try{N.focus();
}catch(ex){}this.setFocus(N);
this.setActive(N);
},
activate:function(N){this.setActive(N);
},
blur:function(N){try{N.blur();
}catch(ex){}
if(this.getActive()===N){this.resetActive();
}
if(this.getFocus()===N){this.resetFocus();
}},
deactivate:function(N){if(this.getActive()===N){this.resetActive();
}},
tryActivate:function(N){var O=this.__em(N);
if(O){this.setActive(O);
}},
__dN:function(K,
P,
L,
Q){var R=qx.event.Registration;
var S=R.createEvent(L,
qx.event.type.Focus,
[K,
P,
Q]);
R.dispatchEvent(K,
S);
},
_windowFocused:true,
__dO:function(){if(this._windowFocused){this._windowFocused=false;
this.__dN(this._window,
null,
b,
false);
}},
__dP:function(){if(!this._windowFocused){this._windowFocused=true;
this.__dN(this._window,
null,
c,
false);
}},
_initObserver:qx.core.Variant.select(a,
{"gecko":function(){this.__dQ=qx.lang.Function.listener(this.__eg,
this);
this.__dR=qx.lang.Function.listener(this.__eh,
this);
this.__dS=qx.lang.Function.listener(this.__ef,
this);
this.__dT=qx.lang.Function.listener(this.__ee,
this);
this.__dU=qx.lang.Function.listener(this.__dY,
this);
this._document.addEventListener(d,
this.__dQ,
true);
this._document.addEventListener(g,
this.__dR,
true);
this._window.addEventListener(c,
this.__dS,
true);
this._window.addEventListener(b,
this.__dT,
true);
this._window.addEventListener(p,
this.__dU,
true);
},
"mshtml":function(){this.__dQ=qx.lang.Function.listener(this.__eg,
this);
this.__dR=qx.lang.Function.listener(this.__eh,
this);
this.__dV=qx.lang.Function.listener(this.__ea,
this);
this.__dW=qx.lang.Function.listener(this.__eb,
this);
this.__dX=qx.lang.Function.listener(this.__ej,
this);
this._document.attachEvent(k,
this.__dQ);
this._document.attachEvent(n,
this.__dR);
this._document.attachEvent(m,
this.__dV);
this._document.attachEvent(l,
this.__dW);
this._document.attachEvent(o,
this.__dX);
},
"webkit":function(){this.__dQ=qx.lang.Function.listener(this.__eg,
this);
this.__dR=qx.lang.Function.listener(this.__eh,
this);
this.__dW=qx.lang.Function.listener(this.__eb,
this);
this.__dS=qx.lang.Function.listener(this.__ef,
this);
this.__dT=qx.lang.Function.listener(this.__ee,
this);
this.__dX=qx.lang.Function.listener(this.__ej,
this);
this._document.addEventListener(d,
this.__dQ,
true);
this._document.addEventListener(g,
this.__dR,
true);
this._document.addEventListener(j,
this.__dX,
false);
this._window.addEventListener(h,
this.__dW,
true);
this._window.addEventListener(c,
this.__dS,
true);
this._window.addEventListener(b,
this.__dT,
true);
},
"opera":function(){this.__dQ=qx.lang.Function.listener(this.__eg,
this);
this.__dR=qx.lang.Function.listener(this.__eh,
this);
this.__dV=qx.lang.Function.listener(this.__ea,
this);
this.__dW=qx.lang.Function.listener(this.__eb,
this);
this._document.addEventListener(d,
this.__dQ,
true);
this._document.addEventListener(g,
this.__dR,
true);
this._window.addEventListener(i,
this.__dV,
true);
this._window.addEventListener(h,
this.__dW,
true);
}}),
_stopObserver:qx.core.Variant.select(a,
{"gecko":function(){this._document.removeEventListener(d,
this.__dQ,
true);
this._document.removeEventListener(g,
this.__dR,
true);
this._window.removeEventListener(c,
this.__dS,
true);
this._window.removeEventListener(b,
this.__dT,
true);
this._window.removeEventListener(p,
this.__dU,
true);
},
"mshtml":function(){this._document.detachEvent(k,
this.__dQ);
this._document.detachEvent(n,
this.__dR);
this._document.detachEvent(m,
this.__dV);
this._document.detachEvent(l,
this.__dW);
this._document.detachEvent(o,
this.__dX);
},
"webkit":function(){this._document.removeEventListener(d,
this.__dQ,
true);
this._document.removeEventListener(j,
this.__dX,
false);
this._window.removeEventListener(i,
this.__dV,
true);
this._window.removeEventListener(h,
this.__dW,
true);
this._window.removeEventListener(c,
this.__dS,
true);
this._window.removeEventListener(b,
this.__dT,
true);
},
"opera":function(){this._document.removeEventListener(d,
this.__dQ,
true);
this._window.removeEventListener(i,
this.__dV,
true);
this._window.removeEventListener(h,
this.__dW,
true);
this._window.removeEventListener(c,
this.__dS,
true);
this._window.removeEventListener(b,
this.__dT,
true);
}}),
__dY:qx.core.Variant.select(a,
{"gecko":function(T){if(!this.__en(T.target)){qx.bom.Event.preventDefault(T);
}},
"default":null}),
__ea:qx.core.Variant.select(a,
{"mshtml":function(T){this.__dP();
var K=T.srcElement;
var U=this.__el(K);
if(U){this.setFocus(U);
}this.tryActivate(K);
},
"opera":function(T){var K=T.target;
if(K==this._document||K==this._window){this.__dP();
if(this.__ec){this.setFocus(this.__ec);
delete this.__ec;
}
if(this.__ed){this.setActive(this.__ed);
delete this.__ed;
}}else{this.setFocus(K);
this.tryActivate(K);
if(!this.__en(K)){K.selectionStart=0;
K.selectionEnd=0;
}}},
"default":null}),
__eb:qx.core.Variant.select(a,
{"mshtml":function(T){if(!T.toElement){this.__dO();
this.resetFocus();
this.resetActive();
}},
"webkit":function(T){var K=T.target;
if(K===this.getFocus()){this.resetFocus();
}
if(K===this.getActive()){this.resetActive();
}},
"opera":function(T){var K=T.target;
if(K==this._document){this.__dO();
this.__ec=this.getFocus();
this.__ed=this.getActive();
this.resetFocus();
this.resetActive();
}else{if(K===this.getFocus()){this.resetFocus();
}
if(K===this.getActive()){this.resetActive();
}}},
"default":null}),
__ee:qx.core.Variant.select(a,
{"gecko":function(T){if(T.target===this._window||T.target===this._document){this.__dO();
this.resetActive();
this.resetFocus();
}},
"webkit":function(T){if(T.target===this._window||T.target===this._document){this.__dO();
this.__ec=this.getFocus();
this.__ed=this.getActive();
this.resetActive();
this.resetFocus();
}},
"default":null}),
__ef:qx.core.Variant.select(a,
{"gecko":function(T){var K=T.target;
if(K===this._window||K===this._document){this.__dP();
K=this._body;
}this.setFocus(K);
this.tryActivate(K);
},
"webkit":function(T){var K=T.target;
if(K===this._window||K===this._document){this.__dP();
if(this.__ec){this.setFocus(this.__ec);
delete this.__ec;
}
if(this.__ed){this.setActive(this.__ed);
delete this.__ed;
}}else{this.setFocus(K);
this.tryActivate(K);
}},
"default":null}),
__eg:qx.core.Variant.select(a,
{"gecko":function(T){var K=T.target;
var U=this.__el(K);
var V=this.__en(K);
if(!V){qx.bom.Event.preventDefault(T);
if(U){U.focus();
}}else if(!U){qx.bom.Event.preventDefault(T);
}},
"mshtml":function(T){var K=T.srcElement;
var U=this.__el(K);
if(U){if(!this.__en(K)){K.unselectable=f;
document.selection.empty();
U.focus();
}}else{qx.bom.Event.preventDefault(T);
if(!this.__en(K)){K.unselectable=f;
}}},
"webkit":function(T){var K=T.target;
var U=this.__el(K);
if(U){this.setFocus(U);
}else{qx.bom.Event.preventDefault(T);
}},
"opera":function(T){var K=T.target;
var U=this.__el(K);
if(!this.__en(K)){qx.bom.Event.preventDefault(T);
if(U){var W=this.getFocus();
if(W&&W.selectionEnd){W.selectionStart=0;
W.selectionEnd=0;
W.blur();
}if(U){this.setFocus(U);
}}}else if(U){this.setFocus(U);
}},
"default":null}),
__eh:qx.core.Variant.select(a,
{"mshtml":function(T){var K=T.srcElement;
if(K.unselectable){K.unselectable=B;
}this.tryActivate(K);
},
"gecko":function(T){var K=T.target;
while(K&&K.offsetWidth===undefined){K=K.parentNode;
}
if(K){this.tryActivate(K);
}if(this.__ei){this.__ei.style.MozUserSelect=y;
this.__ei=null;
}},
"webkit|opera":function(T){this.tryActivate(T.target);
},
"default":null}),
__ej:qx.core.Variant.select(a,
{"mshtml|webkit":function(T){if(!this.__en(T.srcElement)){qx.bom.Event.preventDefault(T);
}},
"default":null}),
__ek:function(X){var Y=qx.bom.element.Attribute.get(X,
A);
if(Y>=1){return true;
}var ba=qx.event.handler.Focus.FOCUSABLE_ELEMENTS;
if(Y>=0&&ba[X.tagName]){return true;
}return false;
},
__el:function(X){while(X&&X.nodeType===1){if(X.getAttribute(H)==f){return null;
}
if(this.__ek(X)){return X;
}X=X.parentNode;
}return this._body;
},
__em:function(X){var bb=X;
while(X&&X.nodeType===1){if(X.getAttribute(I)==f){return null;
}X=X.parentNode;
}return bb;
},
__en:function(bc){while(bc&&bc.nodeType===1){var bd=bc.getAttribute(z);
if(bd!=null){return bd===f;
}bc=bc.parentNode;
}return true;
},
_applyActive:function(be,
bf){if(bf){this.__dN(bf,
be,
v,
true);
}
if(be){this.__dN(be,
bf,
D,
true);
}},
_applyFocus:function(be,
bf){if(bf){this.__dN(bf,
be,
E,
true);
}
if(be){this.__dN(be,
bf,
x,
true);
}if(bf){this.__dN(bf,
be,
b,
false);
}
if(be){this.__dN(be,
bf,
c,
false);
}}},
destruct:function(){this._stopObserver();
this._disposeFields(G,
u,
q,
r,
C,
F);
},
defer:function(bg){qx.event.Registration.addHandler(bg);
var ba=bg.FOCUSABLE_ELEMENTS;
for(var bh in ba){ba[bh.toUpperCase()]=1;
}}});
})();
(function(){var a="qx.event.type.Focus";
qx.Class.define(a,
{extend:qx.event.type.Event,
members:{init:function(b,
c,
d){arguments.callee.base.call(this,
d,
false);
this._target=b;
this._relatedTarget=c;
return this;
}}});
})();
(function(){var a="qx.client",
b="readOnly",
c="accessKey",
d="qx.bom.element.Attribute",
e="rowSpan",
f="vAlign",
g="className",
h="textContent",
i="'",
j="htmlFor",
k="longDesc",
l="cellSpacing",
m="frameBorder",
n="='",
o="",
p="useMap",
q="innerText",
r="innerHTML",
s="tabIndex",
t="dateTime",
u="maxLength",
v="mshtml",
w="cellPadding",
x="colSpan";
qx.Class.define(d,
{statics:{__eo:{names:{"class":g,
"for":j,
html:r,
text:qx.core.Variant.isSet(a,
v)?q:h,
colspan:x,
rowspan:e,
valign:f,
datetime:t,
accesskey:c,
tabindex:s,
maxlength:u,
readonly:b,
longdesc:k,
cellpadding:w,
cellspacing:l,
frameborder:m,
usemap:p},
runtime:{"html":1,
"text":1},
bools:{compact:1,
nowrap:1,
ismap:1,
declare:1,
noshade:1,
checked:1,
disabled:1,
readonly:1,
multiple:1,
selected:1,
noresize:1,
defer:1},
property:{$$html:1,
$$widget:1,
disabled:1,
checked:1,
readOnly:1,
multiple:1,
selected:1,
value:1,
maxLength:1,
className:1,
innerHTML:1,
innerText:1,
textContent:1,
htmlFor:1,
tabIndex:1},
original:{href:1,
src:1,
type:1}},
compile:function(y){var z=[];
var A=this.__eo.runtime;
for(var B in y){if(!A[B]){z.push(B,
n,
y[B],
i);
}}return z.join(o);
},
get:qx.core.Variant.select(a,
{"mshtml":function(C,
D){var E=this.__eo;
var F;
D=E.names[D]||D;
if(E.original[D]){F=C.getAttribute(D,
2);
}else if(E.property[D]){F=C[D];
}else{F=C.getAttribute(D);
}if(E.bools[D]){return !!F;
}return F;
},
"default":function(C,
D){var E=this.__eo;
var F;
D=E.names[D]||D;
if(E.property[D]){F=C[D];
if(F==null){F=C.getAttribute(D);
}}else{F=C.getAttribute(D);
}if(E.bools[D]){return !!F;
}return F;
}}),
set:function(C,
D,
F){var E=this.__eo;
D=E.names[D]||D;
if(E.bools[D]){F=!!F;
}if(E.property[D]){C[D]=F;
}else if(F===true){C.setAttribute(D,
D);
}else if(F===false||F===null){C.removeAttribute(D);
}else{C.setAttribute(D,
F);
}},
reset:function(C,
D){this.set(C,
D,
null);
}}});
})();
(function(){var a="qx.event.type.Native",
b="_native";
qx.Class.define(a,
{extend:qx.event.type.Event,
members:{init:function(c,
d,
e,
f,
g){arguments.callee.base.call(this,
f,
g);
this._target=d||qx.bom.Event.getTarget(c);
this._relatedTarget=e||qx.bom.Event.getRelatedTarget(c);
if(c.timeStamp){this._timeStamp=c.timeStamp;
}this._native=c;
return this;
},
clone:function(h){var i=arguments.callee.base.call(this,
h);
i._native=this._native;
return i;
},
preventDefault:function(){arguments.callee.base.call(this);
qx.bom.Event.preventDefault(this._native);
},
stop:function(){this.stopPropagation();
this.preventDefault();
},
getNativeEvent:function(){return this._native;
}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="qx.event.type.Dom";
qx.Class.define(a,
{extend:qx.event.type.Native,
statics:{SHIFT_MASK:1,
CTRL_MASK:2,
ALT_MASK:4,
META_MASK:8},
members:{getModifiers:function(){if(!this.__modifiers){var b=0;
var c=this._native;
if(c.shiftKey){b|=qx.event.type.Dom.SHIFT_MASK;
}
if(c.ctrlKey){b|=qx.event.type.Dom.CTRL_MASK;
}
if(c.altKey){b|=qx.event.type.Dom.ALT_MASK;
}
if(c.metaKey){b|=qx.event.type.Dom.META_MASK;
}return b;
}return this.__modifiers;
},
isCtrlPressed:function(){return this._native.ctrlKey;
},
isShiftPressed:function(){return this._native.shiftKey;
},
isAltPressed:function(){return this._native.altKey;
},
isMetaPressed:function(){return this._native.metaKey;
},
isCtrlOrCommandPressed:function(){if(qx.bom.client.Platform.MAC){return this._native.metaKey;
}else{return this._native.ctrlKey;
}}}});
})();
(function(){var a="qx.event.type.KeyInput";
qx.Class.define(a,
{extend:qx.event.type.Dom,
members:{init:function(b,
c,
d){arguments.callee.base.call(this,
b,
c,
null,
true,
true);
this._charCode=d;
return this;
},
clone:function(e){var f=arguments.callee.base.call(this,
e);
f._charCode=this._charCode;
return f;
},
getCharCode:function(){return this._charCode;
},
getChar:function(){return String.fromCharCode(this._charCode);
}}});
})();
(function(){var a="iPod",
b="Win32",
c="",
d="Win64",
e="Linux",
f="BSD",
g="Macintosh",
h="iPhone",
i="Windows",
j="qx.bom.client.Platform",
k="X11",
l="MacIntel",
m="MacPPC";
qx.Bootstrap.define(j,
{statics:{NAME:"",
WIN:false,
MAC:false,
UNIX:false,
__ep:function(){var n=navigator.platform;
if(n==null||n===c){n=navigator.userAgent;
}
if(n.indexOf(i)!=-1||n.indexOf(b)!=-1||n.indexOf(d)!=-1){this.WIN=true;
this.NAME="win";
}else if(n.indexOf(g)!=-1||n.indexOf(m)!=-1||n.indexOf(l)!=-1||n.indexOf(a)!=-1||n.indexOf(h)!=-1){this.MAC=true;
this.NAME="mac";
}else if(n.indexOf(k)!=-1||n.indexOf(e)!=-1||n.indexOf(f)!=-1){this.UNIX=true;
this.NAME="unix";
}else{throw new Error("Unable to detect platform: "+n);
}}},
defer:function(o){o.__ep();
}});
})();
(function(){var a="qx.event.type.KeySequence";
qx.Class.define(a,
{extend:qx.event.type.Dom,
members:{init:function(b,
c,
d){arguments.callee.base.call(this,
b,
c,
null,
true,
true);
this._identifier=d;
return this;
},
clone:function(e){var f=arguments.callee.base.call(this,
e);
f._identifier=this._identifier;
return f;
},
getKeyIdentifier:function(){return this._identifier;
}}});
})();
(function(){var a="qx.client",
b="left",
c="right",
d="middle",
e="dblclick",
f="click",
g="none",
h="contextmenu",
i="qx.event.type.Mouse";
qx.Class.define(i,
{extend:qx.event.type.Dom,
members:{init:function(j,
k,
l,
m,
n){arguments.callee.base.call(this,
j,
k,
l,
m,
n);
if(!l){this._relatedTarget=qx.bom.Event.getRelatedTarget(j);
}return this;
},
__eq:qx.core.Variant.select(a,
{"mshtml":{1:b,
2:c,
4:d},
"default":{0:b,
2:c,
1:d}}),
stop:function(){this.stopPropagation();
},
getButton:function(){switch(this._type){case f:case e:return b;
case h:return c;
default:return this.__eq[this._native.button]||g;
}},
isLeftPressed:function(){return this.getButton()===b;
},
isMiddlePressed:function(){return this.getButton()===d;
},
isRightPressed:function(){return this.getButton()===c;
},
getRelatedTarget:function(){return this._relatedTarget;
},
getViewportLeft:function(){return this._native.clientX;
},
getViewportTop:function(){return this._native.clientY;
},
getDocumentLeft:qx.core.Variant.select(a,
{"mshtml":function(){var o=qx.dom.Node.getWindow(this._native.srcElement);
return this._native.clientX+qx.bom.Viewport.getScrollLeft(o);
},
"default":function(){return this._native.pageX;
}}),
getDocumentTop:qx.core.Variant.select(a,
{"mshtml":function(){var o=qx.dom.Node.getWindow(this._native.srcElement);
return this._native.clientY+qx.bom.Viewport.getScrollTop(o);
},
"default":function(){return this._native.pageY;
}}),
getScreenLeft:function(){return this._native.screenX;
},
getScreenTop:function(){return this._native.screenY;
},
getWheelDelta:qx.core.Variant.select(a,
{"default":function(){return -(this._native.wheelDelta/40);
},
"gecko":function(){return this._native.detail;
}})}});
})();
(function(){var a="qx.client",
b="qx.event.type.Drag";
qx.Class.define(b,
{extend:qx.event.type.Event,
members:{init:function(c,
d){arguments.callee.base.call(this,
false,
c);
if(d){this._native=d.getNativeEvent()||null;
this._originalTarget=d.getTarget()||null;
}else{this._native=null;
this._originalTarget=null;
}return this;
},
clone:function(e){var f=arguments.callee.base.call(this,
e);
f._native=this._native;
return f;
},
getDocumentLeft:qx.core.Variant.select(a,
{"mshtml":function(){if(this._native==null){return 0;
}var g=qx.dom.Node.getWindow(this._native.srcElement);
return this._native.clientX+qx.bom.Viewport.getScrollLeft(g);
},
"default":function(){if(this._native==null){return 0;
}return this._native.pageX;
}}),
getDocumentTop:qx.core.Variant.select(a,
{"mshtml":function(){if(this._native==null){return 0;
}var g=qx.dom.Node.getWindow(this._native.srcElement);
return this._native.clientY+qx.bom.Viewport.getScrollTop(g);
},
"default":function(){if(this._native==null){return 0;
}return this._native.pageY;
}}),
getManager:function(){return qx.event.Registration.getManager(this.getTarget()).getHandler(qx.event.handler.DragDrop);
},
addType:function(h){this.getManager().addType(h);
},
addAction:function(i){this.getManager().addAction(i);
},
supportsType:function(h){return this.getManager().supportsType(h);
},
supportsAction:function(i){return this.getManager().supportsAction(i);
},
addData:function(h,
j){this.getManager().addData(h,
j);
},
getData:function(h){return this.getManager().getData(h);
},
getCurrentType:function(){return this.getManager().getCurrentType();
},
getCurrentAction:function(){return this.getManager().getCurrentAction();
}}});
})();
(function(){var a="blur",
b="losecapture",
c="__er",
d="__es",
e="capture",
f="click",
g="__et",
h="qx.event.dispatch.MouseCapture",
j="focus",
k="scroll";
qx.Class.define(h,
{extend:qx.core.Object,
implement:qx.event.IEventDispatcher,
construct:function(m){arguments.callee.base.call(this);
this.__er=m;
this.__es=m.getWindow();
m.addListener(this.__es,
a,
this.releaseCapture,
this);
m.addListener(this.__es,
j,
this.releaseCapture,
this);
m.addListener(this.__es,
k,
this.releaseCapture,
this);
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_FIRST},
members:{__et:null,
__er:null,
__es:null,
canDispatchEvent:function(n,
o,
p){return this.__et&&this.__eu[p];
},
dispatchEvent:function(n,
o,
p){if(p==f){o.stopPropagation();
this.releaseCapture();
return;
}var q=this.__er.getListeners(this.__et,
p,
false);
if(q){o.setCurrentTarget(this.__et);
o.setEventPhase(qx.event.type.Event.AT_TARGET);
for(var r=0,
s=q.length;r<s;r++){var t=q[r].context||o.getCurrentTarget();
q[r].handler.call(t,
o);
}}},
__eu:{"mouseup":1,
"mousedown":1,
"click":1,
"dblclick":1,
"mousemove":1,
"mouseout":1,
"mouseover":1},
activateCapture:function(u){if(this.__et===u){return;
}
if(this.__et){this.releaseCapture();
}this.__et=u;
qx.event.Registration.fireEvent(u,
e,
qx.event.type.Event,
[true,
false]);
},
releaseCapture:function(){var u=this.__et;
if(!u){return;
}this.__et=null;
qx.event.Registration.fireEvent(u,
b,
qx.event.type.Event,
[true,
false]);
}},
destruct:function(){this._disposeFields(g,
c,
d);
},
defer:function(v){qx.event.Registration.addDispatcher(v);
}});
})();
(function(){var a="_window",
b="_manager",
c="qx.event.handler.Window";
qx.Class.define(c,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(d){arguments.callee.base.call(this);
this._manager=d;
this._window=d.getWindow();
this._initWindowObserver();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{error:1,
load:1,
beforeunload:1,
unload:1,
resize:1,
scroll:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_WINDOW,
IGNORE_CAN_HANDLE:true},
members:{canHandleEvent:function(f,
g){},
registerEvent:function(f,
g,
h){},
unregisterEvent:function(f,
g,
h){},
_initWindowObserver:function(){this._onNativeWrapper=qx.lang.Function.listener(this._onNative,
this);
var i=qx.event.handler.Window.SUPPORTED_TYPES;
for(var j in i){qx.bom.Event.addNativeListener(this._window,
j,
this._onNativeWrapper);
}},
_stopWindowObserver:function(){var i=qx.event.handler.Window.SUPPORTED_TYPES;
for(var j in i){qx.bom.Event.removeNativeListener(this._window,
j,
this._onNativeWrapper);
}},
_onNative:function(k){if(this.isDisposed()){return;
}var l=this._window;
var m=l.document;
var n=m.documentElement;
var f=k.target||k.srcElement;
if(f==null||f===l||f===m||f===n){qx.event.Registration.fireEvent(this._window,
k.type);
}}},
destruct:function(){this._stopWindowObserver();
this._disposeFields(b,
a);
},
defer:function(o){qx.event.Registration.addHandler(o);
}});
})();
(function(){var a="textarea",
b="input",
c="qx.client",
d="character",
e="qx.bom.Selection",
f="#text",
g="EndToEnd",
h="button",
i="body";
qx.Class.define(e,
{statics:{getSelectionObject:qx.core.Variant.select(c,
{"mshtml":function(j){return j.selection;
},
"default":function(j){return qx.dom.Node.getWindow(j).getSelection();
}}),
get:qx.core.Variant.select(c,
{"mshtml":function(k){var l=qx.bom.Range.get(qx.dom.Node.getDocument(k));
return l.text;
},
"default":function(k){if(qx.dom.Node.isElement(k)&&(k.nodeName.toLowerCase()==b||k.nodeName.toLowerCase()==a)){return k.value.substring(k.selectionStart,
k.selectionEnd);
}else{return qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(k)).toString();
}return null;
}}),
getLength:qx.core.Variant.select(c,
{"mshtml":function(k){var m=qx.bom.Selection.get(k);
var n=qx.util.StringSplit.split(m,
/\r\n/);
return m.length-(n.length-1);
},
"opera":function(k){var m,
o,
n;
if(qx.dom.Node.isElement(k)&&(k.nodeName.toLowerCase()==b||k.nodeName.toLowerCase()==a)){var p=k.selectionStart;
var q=k.selectionEnd;
m=k.value.substring(p,
q);
o=q-p;
}else{m=qx.bom.Selection.get(k);
o=m.length;
}n=qx.util.StringSplit.split(m,
/\r\n/);
return o-(n.length-1);
},
"default":function(k){if(qx.dom.Node.isElement(k)&&(k.nodeName.toLowerCase()==b||k.nodeName.toLowerCase()==a)){return k.selectionEnd-k.selectionStart;
}else{return qx.bom.Selection.get(k).length;
}return null;
}}),
set:qx.core.Variant.select(c,
{"mshtml":function(k,
p,
q){var l;
if(qx.dom.Node.isDocument(k)){k=k.body;
}
if(qx.dom.Node.isElement(k)||qx.dom.Node.isText(k)){switch(k.nodeName.toLowerCase()){case b:case a:case h:if(q===undefined){q=k.value.length;
}
if(p>=0&&p<=k.value.length&&q>=0&&q<=k.value.length){l=qx.bom.Range.get(k);
l.collapse(true);
l.moveStart(d,
p);
l.moveEnd(d,
q);
l.select();
return true;
}break;
case f:if(q===undefined){q=k.nodeValue.length;
}
if(p>=0&&p<=k.nodeValue.length&&q>=0&&q<=k.nodeValue.length){l=qx.bom.Range.get(qx.dom.Node.getBodyElement(k));
l.moveToElementText(k.parentNode);
l.collapse(true);
l.moveStart(d,
p);
l.moveEnd(d,
q);
l.select();
return true;
}break;
default:if(q===undefined){q=k.childNodes.length-1;
}if(k.childNodes[p]&&k.childNodes[q]){l=qx.bom.Range.get(qx.dom.Node.getBodyElement(k));
l.moveToElementText(k.childNodes[p]);
l.collapse(true);
var r=qx.bom.Range.get(qx.dom.Node.getBodyElement(k));
r.moveToElementText(k.childNodes[q]);
l.setEndPoint(g,
r);
l.select();
return true;
}}}return false;
},
"default":function(k,
p,
q){var s=k.nodeName.toLowerCase();
if(qx.dom.Node.isElement(k)&&(s==b||s==a)){if(q===undefined){q=k.value.length;
}if(p>=0&&p<=k.value.length&&q>=0&&q<=k.value.length){k.select();
k.setSelectionRange(p,
q);
return true;
}}else{var t=false;
var u=qx.dom.Node.getWindow(k).getSelection();
var l=qx.bom.Range.get(k);
if(qx.dom.Node.isText(k)){if(q===undefined){q=k.length;
}
if(p>=0&&p<k.length&&q>=0&&q<=k.length){t=true;
}}else if(qx.dom.Node.isElement(k)){if(q===undefined){q=k.childNodes.length-1;
}
if(p>=0&&k.childNodes[p]&&q>=0&&k.childNodes[q]){t=true;
}}else if(qx.dom.Node.isDocument(k)){k=k.body;
if(q===undefined){q=k.childNodes.length-1;
}
if(p>=0&&k.childNodes[p]&&q>=0&&k.childNodes[q]){t=true;
}}
if(t){if(!u.isCollapsed){u.collapseToStart();
}l.setStart(k,
p);
if(qx.dom.Node.isText(k)){l.setEnd(k,
q);
}else{l.setEndAfter(k.childNodes[q]);
}if(u.rangeCount>0){u.removeAllRanges();
}u.addRange(l);
return true;
}}return false;
}}),
setAll:function(k){return qx.bom.Selection.set(k,
0);
},
clear:qx.core.Variant.select(c,
{"mshtml":function(k){var u=qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(k));
var l=qx.bom.Range.get(k);
var v=l.parentElement();
var w=qx.bom.Range.get(qx.dom.Node.getDocument(k));
if(v==w.parentElement()&&v==k){u.empty();
}},
"default":function(k){var u=qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(k));
var s=k.nodeName.toLowerCase();
if(qx.dom.Node.isElement(k)&&(s==b||s==a)){k.setSelectionRange(0,
0);
qx.bom.Element.blur(k);
}else if(qx.dom.Node.isDocument(k)||s==i){u.collapse(k.body?k.body:k,
0);
}else{var l=qx.bom.Range.get(k);
if(!l.collapsed){var x;
var y=l.commonAncestorContainer;
if(qx.dom.Node.isElement(k)&&qx.dom.Node.isText(y)){x=y.parentNode;
}else{x=y;
}
if(x==k){u.collapse(k,
0);
}}}}})}});
})();
(function(){var a="button",
b="qx.bom.Range",
c="text",
d="password",
e="file",
f="submit",
g="reset",
h="textarea",
i="input",
j="hidden",
k="qx.client",
l="body";
qx.Class.define(b,
{statics:{get:qx.core.Variant.select(k,
{"mshtml":function(m){if(qx.dom.Node.isElement(m)){switch(m.nodeName.toLowerCase()){case i:switch(m.type){case c:case d:case j:case a:case g:case e:case f:return m.createTextRange();
break;
default:return qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(m)).createRange();
}break;
case h:case l:case a:return m.createTextRange();
break;
default:return qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(m)).createRange();
}}else{return qx.bom.Selection.getSelectionObject(qx.dom.Node.getDocument(m)).createRange();
}},
"default":function(m){var n=qx.dom.Node.getDocument(m);
var o=qx.bom.Selection.getSelectionObject(n);
if(o.rangeCount>0){return o.getRangeAt(0);
}else{return n.createRange();
}}})}});
})();
(function(){var a="",
b="g",
c="$",
d="qx.util.StringSplit",
e="\\$&",
f="^";
qx.Bootstrap.define(d,
{statics:{split:function(g,
h,
k){var l=a;
if(h===undefined){return [g.toString()];
}else if(h===null||h.constructor!==RegExp){h=new RegExp(String(h).replace(/[.*+?^${}()|[\]\/\\]/g,
e),
b);
}else{l=h.toString().replace(/^[\S\s]+\//,
a);
if(!h.global){h=new RegExp(h.source,
b+l);
}}var m=new RegExp(f+h.source+c,
l);
if(k===undefined||+k<0){k=false;
}else{k=Math.floor(+k);
if(!k){return [];
}}var n,
o=[],
p=0,
q=0;
while((k?q++<=k:true)&&(n=h.exec(g))){if((n[0].length===0)&&(h.lastIndex>n.index)){h.lastIndex--;
}
if(h.lastIndex>p){if(n.length>1){n[0].replace(m,
function(){for(var r=1;r<arguments.length-2;r++){if(arguments[r]===undefined){n[r]=undefined;
}}});
}o=o.concat(g.substring(p,
n.index),
(n.index===g.length?[]:n.slice(1)));
p=h.lastIndex;
}
if(n[0].length===0){h.lastIndex++;
}}return (p===g.length)?(h.test(a)?o:o.concat(a)):(k?o:o.concat(g.substring(p)));
}}});
})();
(function(){var a="qx.ui.core.queue.Widget",
b="widget";
qx.Class.define(a,
{statics:{__ev:{},
add:function(c){var d=this.__ev;
if(d[c.$$hash]){return;
}d[c.$$hash]=c;
qx.ui.core.queue.Manager.scheduleFlush(b);
},
flush:function(){var d=this.__ev;
var e;
for(var f in d){e=d[f];
delete d[f];
e.syncWidget();
}for(var f in d){return;
}this.__ev={};
}}});
})();
(function(){var a="appearance",
b="qx.ui.core.queue.Appearance";
qx.Class.define(b,
{statics:{__ew:{},
add:function(c){var d=this.__ew;
if(d[c.$$hash]){return;
}d[c.$$hash]=c;
qx.ui.core.queue.Manager.scheduleFlush(a);
},
flush:function(){var d=this.__ew;
var e;
for(var f in d){e=d[f];
delete d[f];
e.syncAppearance();
}for(var f in d){return;
}this.__ew={};
}}});
})();
(function(){var a="qx.ui.core.queue.Layout",
b="layout";
qx.Class.define(a,
{statics:{__ex:{},
add:function(c){this.__ex[c.$$hash]=c;
qx.ui.core.queue.Manager.scheduleFlush(b);
},
flush:function(){var d=this.__eB();
for(var e=d.length-1;e>=0;e--){var c=d[e];
if(c.hasValidLayout()){continue;
}if(c.isRootWidget()&&!c.hasUserBounds()){var f=c.getSizeHint();
c.renderLayout(0,
0,
f.width,
f.height);
}else{var g=c.getBounds();
c.renderLayout(g.left,
g.top,
g.width,
g.height);
}}},
getNestingLevel:function(c){var h=this.__eA;
var j=0;
var k=c;
while(true){if(h[k.$$hash]!=null){j+=h[k.$$hash];
break;
}
if(!k.$$parent){break;
}k=k.$$parent;
j+=1;
}var l=j;
while(c&&c!==k){h[c.$$hash]=l--;
c=c.$$parent;
}return j;
},
isWidgetVisible:function(c){var h=this.__ez;
var k=c;
var m=false;
while(k){if(h[k.$$hash]!=null){m=h[k.$$hash];
break;
}if(k.isRootWidget()){m=true;
break;
}if(!k.shouldBeLayouted()){break;
}k=k.$$parent;
}while(c&&c!==k){h[c.$$hash]=m;
c=c.$$parent;
}return m;
},
__ey:function(){this.__ez={};
this.__eA={};
var n=[];
var d=this.__ex;
var c,
j;
for(var o in d){c=d[o];
if(this.isWidgetVisible(c)){j=this.getNestingLevel(c);
if(!n[j]){n[j]={};
}n[j][o]=c;
delete d[o];
}}return n;
},
__eB:function(){var p=[];
var n=this.__ey();
for(var j=n.length-1;j>=0;j--){if(!n[j]){continue;
}
for(var o in n[j]){var c=n[j][o];
if(j==0||c.isRootWidget()||c.hasUserBounds()){p.push(c);
c.invalidateLayoutCache();
continue;
}var q=c.getSizeHint(false);
if(q){c.invalidateLayoutCache();
var r=c.getSizeHint();
var s=(!c.getBounds()||q.minWidth!==r.minWidth||q.width!==r.width||q.maxWidth!==r.maxWidth||q.minHeight!==r.minHeight||q.height!==r.height||q.maxHeight!==r.maxHeight);
}else{s=true;
}
if(s){var k=c.getLayoutParent();
if(!n[j-1]){n[j-1]={};
}n[j-1][k.$$hash]=k;
}else{p.push(c);
}}}return p;
}}});
})();
(function(){var a="dispose",
b="qx.ui.core.queue.Dispose";
qx.Class.define(b,
{statics:{__eC:{},
add:function(c){var d=this.__eC;
if(d[c.$$hash]){return;
}d[c.$$hash]=c;
qx.ui.core.queue.Manager.scheduleFlush(a);
},
flush:function(){var d=this.__eC;
for(var e in d){d[e].dispose();
delete d[e];
}for(var e in d){return;
}this.__eC={};
}}});
})();
(function(){var a="qx.ui.core.MChildrenHandling";
qx.Mixin.define(a,
{members:{getChildren:function(){return this._getChildren();
},
hasChildren:function(){return this._hasChildren();
},
indexOf:function(b){return this._indexOf(b);
},
add:function(b,
c){this._add(b,
c);
},
addAt:function(b,
d,
c){this._addAt(b,
d,
c);
},
addBefore:function(b,
e,
c){this._addBefore(b,
e,
c);
},
addAfter:function(b,
f,
c){this._addAfter(b,
f,
c);
},
remove:function(b){this._remove(b);
},
removeAt:function(d){this._removeAt(d);
},
removeAll:function(){this._removeAll();
}},
statics:{remap:function(g){g.getChildren=g._getChildren;
g.hasChildren=g._hasChildren;
g.indexOf=g._indexOf;
g.add=g._add;
g.addAt=g._addAt;
g.addBefore=g._addBefore;
g.addAfter=g._addAfter;
g.remove=g._remove;
g.removeAt=g._removeAt;
g.removeAll=g._removeAll;
}}});
})();
(function(){var a="Integer",
b="_applyDimension",
c="Boolean",
d="_applyStretching",
e="_applyMargin",
f="shorthand",
g="_applyAlign",
h="allowShrinkY",
i="__eI",
j="bottom",
k="baseline",
l="marginBottom",
m="qx.ui.core.LayoutItem",
n="center",
o="marginTop",
p="$$subparent",
q="allowGrowX",
r="middle",
s="marginLeft",
t="allowShrinkX",
u="__eJ",
v="$$parent",
w="top",
x="right",
y="marginRight",
z="__eG",
A="abstract",
B="__eE",
C="allowGrowY",
D="left";
qx.Class.define(m,
{type:A,
extend:qx.core.Object,
properties:{minWidth:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
width:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
maxWidth:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
minHeight:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
height:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
maxHeight:{check:a,
nullable:true,
apply:b,
init:null,
themeable:true},
allowGrowX:{check:c,
apply:d,
init:true,
themeable:true},
allowShrinkX:{check:c,
apply:d,
init:true,
themeable:true},
allowGrowY:{check:c,
apply:d,
init:true,
themeable:true},
allowShrinkY:{check:c,
apply:d,
init:true,
themeable:true},
allowStretchX:{group:[q,
t],
mode:f,
themeable:true},
allowStretchY:{group:[C,
h],
mode:f,
themeable:true},
marginTop:{check:a,
init:0,
apply:e,
themeable:true},
marginRight:{check:a,
init:0,
apply:e,
themeable:true},
marginBottom:{check:a,
init:0,
apply:e,
themeable:true},
marginLeft:{check:a,
init:0,
apply:e,
themeable:true},
margin:{group:[o,
y,
l,
s],
mode:f,
themeable:true},
alignX:{check:[D,
n,
x],
nullable:true,
apply:g,
themeable:true},
alignY:{check:[w,
r,
j,
k],
nullable:true,
apply:g,
themeable:true}},
members:{__eD:null,
__eE:null,
__eF:null,
__eG:null,
__eH:null,
__eI:null,
__eJ:null,
getBounds:function(){return this.__eI||this.__eE||null;
},
clearSeparators:function(){},
renderSeparator:function(E,
F){},
renderLayout:function(G,
H,
I,
J){var K;
var L=null;
if(this.getHeight()==null&&this._hasHeightForWidth()){var L=this._getHeightForWidth(I);
}
if(L!=null&&L!==this.__eD){this.__eD=L;
qx.ui.core.queue.Layout.add(this);
return null;
}var M=this.__eE;
if(!M){M=this.__eE={};
}var N={};
if(G!==M.left||H!==M.top){N.position=true;
M.left=G;
M.top=H;
}
if(I!==M.width||J!==M.height){N.size=true;
M.width=I;
M.height=J;
}if(this.__eF){N.local=true;
delete this.__eF;
}
if(this.__eH){N.margin=true;
delete this.__eH;
}return N;
},
shouldBeLayouted:function(){return true;
},
hasValidLayout:function(){return !this.__eF;
},
scheduleLayoutUpdate:function(){qx.ui.core.queue.Layout.add(this);
},
invalidateLayoutCache:function(){this.__eF=true;
this.__eG=null;
},
getSizeHint:function(O){var P=this.__eG;
if(P){return P;
}
if(O===false){return null;
}P=this.__eG=this._computeSizeHint();
if(this.__eD&&this.getHeight()==null){P.height=this.__eD;
}if(!this.getAllowShrinkX()){P.minWidth=P.width;
}else if(P.minWidth>P.width){P.width=P.minWidth;
}
if(!this.getAllowShrinkY()){P.minHeight=P.height;
}else if(P.minHeight>P.height){P.height=P.minHeight;
}if(!this.getAllowGrowX()){P.maxWidth=P.width;
}else if(P.width>P.maxWidth){P.width=P.maxWidth;
}
if(!this.getAllowGrowY()){P.maxHeight=P.height;
}else if(P.height>P.maxHeight){P.height=P.maxHeight;
}return P;
},
_computeSizeHint:function(){var Q=this.getMinWidth()||0;
var R=this.getMinHeight()||0;
var I=this.getWidth()||Q;
var J=this.getHeight()||R;
var S=this.getMaxWidth()||Infinity;
var T=this.getMaxHeight()||Infinity;
return {minWidth:Q,
width:I,
maxWidth:S,
minHeight:R,
height:J,
maxHeight:T};
},
_hasHeightForWidth:function(){return false;
},
_getHeightForWidth:function(I){return null;
},
_applyMargin:function(){this.__eH=true;
var U=this.$$parent;
if(U){U.updateLayoutProperties();
}},
_applyAlign:function(){var U=this.$$parent;
if(U){U.updateLayoutProperties();
}},
_applyDimension:function(){qx.ui.core.queue.Layout.add(this);
},
_applyStretching:function(){qx.ui.core.queue.Layout.add(this);
},
hasUserBounds:function(){return !!this.__eI;
},
setUserBounds:function(G,
H,
I,
J){this.__eI={left:G,
top:H,
width:I,
height:J};
qx.ui.core.queue.Layout.add(this);
},
resetUserBounds:function(){delete this.__eI;
qx.ui.core.queue.Layout.add(this);
},
__eK:{},
setLayoutProperties:function(V){if(V==null){return;
}var W=this.__eJ;
if(!W){W=this.__eJ={};
}var U=this.getLayoutParent();
if(U){U.updateLayoutProperties(V);
}for(var X in V){if(V[X]==null){delete W[X];
}else{W[X]=V[X];
}}},
getLayoutProperties:function(){return this.__eJ||this.__eK;
},
clearLayoutProperties:function(){delete this.__eJ;
},
updateLayoutProperties:function(V){var Y=this._getLayout();
if(Y){var X;
Y.invalidateChildrenCache();
}qx.ui.core.queue.Layout.add(this);
},
getApplicationRoot:function(){return qx.core.Init.getApplication().getRoot();
},
getLayoutParent:function(){return this.$$parent||null;
},
setLayoutParent:function(U){this.$$parent=U;
},
isRootWidget:function(){return false;
},
_getRoot:function(){var U=this;
while(U){if(U.isRootWidget()){return U;
}U=U.$$parent;
}return null;
},
clone:function(){var ba=arguments.callee.base.call(this);
var V=this.__eJ;
if(V){ba.__eJ=qx.lang.Object.copy(V);
}return ba;
},
serialize:function(){var bb=arguments.callee.base.call(this);
var V=this.__eJ;
if(V){bb.layoutProperties=qx.lang.Object.copy(V);
}return bb;
}},
destruct:function(){this._disposeFields(v,
p,
u,
B,
i,
z);
}});
})();
(function(){var a="px",
b="qx.event.type.Mouse",
c="Boolean",
d="qx.event.type.Drag",
f="visible",
g="qx.event.type.Focus",
h="excluded",
j="Integer",
k="on",
m="object",
n="_applyPadding",
o="qx.event.type.Event",
p="zIndex",
q="hidden",
r="tabIndex",
s="contextmenu",
t="absolute",
u="backgroundColor",
v="focused",
w="hovered",
x="qx.event.type.KeySequence",
y="qx.client",
z="height",
A="div",
B="qx.event.type.Data",
C="disabled",
D="move",
E="dragstart",
F="dragchange",
G="position",
H="dragend",
I="resize",
J="Decorator",
K="width",
L="$$widget",
M="mshtml",
N="none",
O="default",
P="Color",
Q="top",
R="left",
S="String",
T="drag",
U="__eT",
V="_applyBackgroundColor",
W="_applyFocusable",
X="changeShadow",
Y="qx.event.type.KeyInput",
ba="__eO",
bb="normal",
bc="__fc",
bd="Font",
be="__eQ",
bf="_applyShadow",
bg="_applyEnabled",
bh="_applySelectable",
bi="_applyKeepActive",
bj="Number",
bk="_applyVisibility",
bl="repeat",
bm="qxDraggable",
bn="__eR",
bo="paddingLeft",
bp="_applyDroppable",
bq="userSelect",
br="_applyCursor",
bs="changeVisibility",
bt="_applyDraggable",
bu="changeTextColor",
bv="changeContextMenu",
bw="paddingTop",
bx="opacity",
by="hideFocus",
bz="outline",
bA="_applyAppearance",
bB="overflowX",
bC="_applyOpacity",
bD="url(",
bE=")",
bF="qx.ui.core.Widget",
bG="__eP",
bH="_applyFont",
bI="cursor",
bJ="qxDroppable",
bK="changeZIndex",
bL="overflowY",
bM="changeEnabled",
bN="changeFont",
bO="off",
bP="_applyDecorator",
bQ="_applyZIndex",
bR="_applyTextColor",
bS="qx.ui.menu.Menu",
bT="true",
bU="widget",
bV="__eV",
bW="changeDecorator",
bX="__eM",
bY="_applyTabIndex",
ca="changeAppearance",
cb="shorthand",
cc="/",
cd="_applyContextMenu",
ce="qxSelectable",
cf="__eL",
cg="paddingBottom",
ch="__eW",
ci="qx.ui.tooltip.ToolTip",
cj="qxKeepActive",
ck="_applyKeepFocus",
cl="webkit",
cm="paddingRight",
cn="changeBackgroundColor",
co="qxKeepFocus",
cp="undefined",
cq="qx/static/blank.gif";
qx.Class.define(bF,
{extend:qx.ui.core.LayoutItem,
include:[qx.locale.MTranslation],
construct:function(){arguments.callee.base.call(this);
this.__eL=this._createContainerElement();
this.__eM=this.__eU();
this.__eL.add(this.__eM);
this.__eL.setAttribute(L,
this.toHashCode());
{};
qx.ui.core.queue.Appearance.add(this);
this.initFocusable();
this.initSelectable();
this.initCursor();
this.initKeepFocus();
this.initKeepActive();
},
events:{appear:o,
disappear:o,
resize:B,
move:B,
mousemove:b,
mouseover:b,
mouseout:b,
mousedown:b,
mouseup:b,
click:b,
dblclick:b,
contextmenu:b,
mousewheel:b,
keyup:x,
keydown:x,
keypress:x,
keyinput:Y,
focus:g,
blur:g,
focusin:g,
focusout:g,
activate:g,
deactivate:g,
capture:o,
losecapture:o,
drop:d,
dragleave:d,
dragover:d,
drag:d,
dragstart:d,
dragend:d,
dragchange:d,
droprequest:d},
properties:{paddingTop:{check:j,
init:0,
apply:n,
themeable:true},
paddingRight:{check:j,
init:0,
apply:n,
themeable:true},
paddingBottom:{check:j,
init:0,
apply:n,
themeable:true},
paddingLeft:{check:j,
init:0,
apply:n,
themeable:true},
padding:{group:[bw,
cm,
cg,
bo],
mode:cb,
themeable:true},
zIndex:{nullable:true,
init:null,
apply:bQ,
event:bK,
check:j,
themeable:true},
decorator:{nullable:true,
init:null,
apply:bP,
event:bW,
check:J,
themeable:true},
shadow:{nullable:true,
init:null,
apply:bf,
event:X,
check:J,
themeable:true},
backgroundColor:{nullable:true,
check:P,
apply:V,
event:cn,
themeable:true},
textColor:{nullable:true,
check:P,
apply:bR,
event:bu,
themeable:true,
inheritable:true},
font:{nullable:true,
apply:bH,
check:bd,
event:bN,
themeable:true,
inheritable:true},
opacity:{check:bj,
apply:bC,
themeable:true,
nullable:true,
init:null},
cursor:{check:S,
apply:br,
themeable:true,
inheritable:true,
nullable:true,
init:null},
toolTip:{check:ci,
nullable:true},
visibility:{check:[f,
q,
h],
init:f,
apply:bk,
event:bs},
enabled:{init:true,
check:c,
inheritable:true,
apply:bg,
event:bM},
anonymous:{init:false,
check:c},
tabIndex:{check:j,
nullable:true,
apply:bY},
focusable:{check:c,
init:false,
apply:W},
keepFocus:{check:c,
init:false,
apply:ck},
keepActive:{check:c,
init:false,
apply:bi},
draggable:{check:c,
init:false,
apply:bt},
droppable:{check:c,
init:false,
apply:bp},
selectable:{check:c,
init:false,
apply:bh},
contextMenu:{check:bS,
apply:cd,
nullable:true,
event:bv},
appearance:{check:S,
init:bU,
apply:bA,
event:ca}},
statics:{DEBUG:false,
getWidgetByElement:function(cr){try{while(cr){var cs=cr.$$widget;
if(cs!=null){return qx.core.ObjectRegistry.fromHashCode(cs);
}cr=cr.parentNode;
}}catch(ex){}return null;
},
contains:function(ct,
cu){while(cu){if(ct==cu){return true;
}cu=cu.getLayoutParent();
}return false;
},
__eN:{}},
members:{__eL:null,
__eM:null,
__eO:null,
__eP:null,
__eQ:null,
__eR:null,
_getLayout:function(){return this.__eR;
},
_setLayout:function(cv){{};
if(this.__eR){this.__eR.connectToWidget(null);
}
if(cv){cv.connectToWidget(this);
}this.__eR=cv;
qx.ui.core.queue.Layout.add(this);
},
setLayoutParent:function(ct){if(this.$$parent===ct){return;
}
if(this.$$parent){this.$$parent.getContentElement().remove(this.__eL);
}this.$$parent=ct||null;
if(this.$$parent){this.$$parent.getContentElement().add(this.__eL);
}qx.core.Property.refresh(this);
},
__eS:null,
renderLayout:function(cw,
cx,
cy,
cz){var cA=arguments.callee.base.call(this,
cw,
cx,
cy,
cz);
if(!cA){return;
}var cB=this.__eL;
var cC=this.__eM;
var cD=cA.size||this.__eS;
var cE=a;
if(cA.position){cB.setStyle(R,
cw+cE);
cB.setStyle(Q,
cx+cE);
}if(cA.size){cB.setStyle(K,
cy+cE);
cB.setStyle(z,
cz+cE);
}
if(cD||cA.local||cA.margin){var cF=this.getInsets();
var cG=cy-cF.left-cF.right;
var cH=cz-cF.top-cF.bottom;
}
if(this.__eS){cC.setStyle(R,
cF.left+cE);
cC.setStyle(Q,
cF.top+cE);
}
if(cD){cC.setStyle(K,
cG+cE);
cC.setStyle(z,
cH+cE);
}
if(cA.size){var cI=this.__eQ;
if(cI){cI.setStyles({width:cy+a,
height:cz+a});
}}
if(cA.size||this.__eS){var cJ=qx.theme.manager.Decoration.getInstance();
var cK=this.getDecorator();
if(cK){var cr=this.__eO;
var cL=cJ.resolve(cK);
cL.resize(cr,
cy,
cz);
}}
if(cA.size){var cM=this.getShadow();
if(cM){var cr=this.__eP;
var cL=cJ.resolve(cM);
var cF=cL.getInsets();
var cN=cy+cF.left+cF.right;
var cO=cz+cF.top+cF.bottom;
cL.resize(cr,
cN,
cO);
}}
if(cD||cA.local||cA.margin){if(this.__eR&&this.hasLayoutChildren()){this.__eR.renderLayout(cG,
cH);
}else if(this.hasLayoutChildren()){throw new Error("At least one child in control "+this._findTopControl()+" requires a layout, but no one was defined!");
}}if(cA.position&&this.hasListener(D)){this.fireDataEvent(D,
this.getBounds());
}
if(cA.size&&this.hasListener(I)){this.fireDataEvent(I,
this.getBounds());
}delete this.__eS;
},
__eT:null,
clearSeparators:function(){var cP=this.__eT;
if(!cP){return;
}var cQ=qx.ui.core.Widget.__eN;
var cC=this.__eM;
var cR,
cS;
for(var cT=0,
cU=cP.length;cT<cU;cT++){cS=cP[cT];
cR=cS.$$separator;
if(!cQ[cR]){cQ[cR]=[cS];
}else{cQ[cR].push(cS);
}cC.remove(cS);
}cP.length=0;
},
renderSeparator:function(cR,
cV){var cQ=qx.ui.core.Widget.__eN;
var cW=qx.theme.manager.Decoration.getInstance();
if(typeof cR==m){var cX=cR.toHashCode();
var cL=cR;
}else{var cX=cR;
var cL=cW.resolve(cR);
}var cY=cQ[cR];
if(cY&&cY.length>0){var cS=cY.pop();
}else{var cS=this.__fb(cL);
}this.__eM.add(cS);
cL.resize(cS,
cV.width,
cV.height);
var da=cS.getDomElement().style;
da.left=cV.left+a;
da.top=cV.top+a;
if(!this.__eT){this.__eT=[cS];
}else{this.__eT.push(cS);
}cS.$$separator=cX;
},
_computeSizeHint:function(){var cy=this.getWidth();
var db=this.getMinWidth();
var dc=this.getMaxWidth();
var cz=this.getHeight();
var dd=this.getMinHeight();
var de=this.getMaxHeight();
var df=this._getContentHint();
var cF=this.getInsets();
var dg=cF.left+cF.right;
var dh=cF.top+cF.bottom;
if(cy==null){cy=df.width+dg;
}
if(cz==null){cz=df.height+dh;
}
if(db==null){db=dg;
if(df.minWidth!=null){db+=df.minWidth;
}}
if(dd==null){dd=dh;
if(df.minHeight!=null){dd+=df.minHeight;
}}
if(dc==null){if(df.maxWidth==null){dc=Infinity;
}else{dc=df.maxWidth+dg;
}}
if(de==null){if(df.maxHeight==null){de=Infinity;
}else{de=df.maxHeight+dh;
}}return {width:cy,
minWidth:db,
maxWidth:dc,
height:cz,
minHeight:dd,
maxHeight:de};
},
invalidateLayoutCache:function(){arguments.callee.base.call(this);
if(this.__eR){this.__eR.invalidateLayoutCache();
}},
_getContentHint:function(){var cv=this.__eR;
if(cv){if(this.hasLayoutChildren()){var di=cv.getSizeHint();
var dj;
return di;
}else{return {width:0,
height:0};
}}else{return {width:100,
height:50};
}},
_getHeightForWidth:function(cy){var cF=this.getInsets();
var dg=cF.left+cF.right;
var dh=cF.top+cF.bottom;
var dk=cy-dg;
var dl=this._getContentHeightForWidth(dk);
var cz=dl+dh;
return cz;
},
_getContentHeightForWidth:function(cy){throw new Error("Abstract method call: _getContentHeightForWidth()!");
},
getInsets:function(){var cx=this.getPaddingTop();
var dm=this.getPaddingRight();
var dn=this.getPaddingBottom();
var cw=this.getPaddingLeft();
var cK=this.getDecorator();
if(cK){var cJ=qx.theme.manager.Decoration.getInstance();
var cL=cJ.resolve(cK);
var dp=cL.getInsets();
{};
cx+=dp.top;
dm+=dp.right;
dn+=dp.bottom;
cw+=dp.left;
}return {"top":cx,
"right":dm,
"bottom":dn,
"left":cw};
},
getInnerSize:function(){var dq=this.getBounds();
if(!dq){return null;
}var cF=this.getInsets();
return {width:dq.width-cF.left-cF.right,
height:dq.height-cF.top-cF.bottom};
},
show:function(){this.setVisibility(f);
},
hide:function(){this.setVisibility(q);
},
exclude:function(){this.setVisibility(h);
},
isVisible:function(){return this.getVisibility()===f;
},
isHidden:function(){return this.getVisibility()!==f;
},
isExcluded:function(){return this.getVisibility()===h;
},
_createContainerElement:function(){var dr=new qx.html.Element(A);
{};
dr.setStyle(G,
t);
dr.setStyle(p,
0);
return dr;
},
__eU:function(){var dr=this._createContentElement();
{};
dr.setStyle(G,
t);
dr.setStyle(p,
10);
return dr;
},
_createContentElement:function(){var dr=new qx.html.Element(A);
dr.setStyle(bB,
q);
dr.setStyle(bL,
q);
return dr;
},
getContainerElement:function(){return this.__eL;
},
getContentElement:function(){return this.__eM;
},
getDecoratorElement:function(){return this.__eO;
},
__eV:null,
__eW:null,
getLayoutChildren:function(){var ds=this.__eV;
if(!ds){return this.__eX;
}
if(this.__eW){return this.__eW;
}var dt=[];
for(var cT=0,
cU=ds.length;cT<cU;cT++){var cu=ds[cT];
if(!cu.hasUserBounds()&&cu.shouldBeLayouted()){dt.push(cu);
}}this.__eW=dt;
return dt;
},
scheduleLayoutUpdate:function(){qx.ui.core.queue.Layout.add(this);
},
invalidateLayoutChildren:function(){var cv=this.__eR;
if(cv){cv.invalidateChildrenCache();
}this.__eW=null;
qx.ui.core.queue.Layout.add(this);
},
shouldBeLayouted:function(){return this.getVisibility()!==h;
},
hasLayoutChildren:function(){var ds=this.getLayoutChildren();
return ds&&ds.length>0;
},
getChildrenContainer:function(){return this;
},
__eX:[],
_getChildren:function(){return this.__eV||this.__eX;
},
_indexOf:function(cu){var ds=this.__eV;
if(!ds){return -1;
}return ds.indexOf(cu);
},
_hasChildren:function(){var ds=this.__eV;
return ds&&(!!ds[0]);
},
_add:function(cu,
du){if(cu.getLayoutParent()==this){qx.lang.Array.remove(this.__eV,
cu);
}
if(this.__eV){this.__eV.push(cu);
}else{this.__eV=[cu];
}this.__eY(cu,
du);
},
_addAt:function(cu,
dv,
du){if(!this.__eV){this.__eV=[];
}if(cu.getLayoutParent()==this){qx.lang.Array.remove(this.__eV,
cu);
}var dw=this.__eV[dv];
if(dw===cu){return cu.setLayoutProperties(du);
}
if(dw){qx.lang.Array.insertBefore(this.__eV,
cu,
dw);
}else{this.__eV.push(cu);
}this.__eY(cu,
du);
},
_addBefore:function(cu,
dx,
du){{};
if(cu==dx){return;
}
if(!this.__eV){this.__eV=[];
}if(cu.getLayoutParent()==this){qx.lang.Array.remove(this.__eV,
cu);
}qx.lang.Array.insertBefore(this.__eV,
cu,
dx);
this.__eY(cu,
du);
},
_addAfter:function(cu,
dy,
du){{};
if(cu==dy){return;
}
if(!this.__eV){this.__eV=[];
}if(cu.getLayoutParent()==this){qx.lang.Array.remove(this.__eV,
cu);
}qx.lang.Array.insertAfter(this.__eV,
cu,
dy);
this.__eY(cu,
du);
},
_remove:function(cu){if(!this.__eV){return;
}qx.lang.Array.remove(this.__eV,
cu);
this.__fa(cu);
},
_removeAt:function(dv){if(!this.__eV){throw new Error("This widget has no children!");
}var cu=this.__eV[dv];
qx.lang.Array.removeAt(this.__eV,
dv);
this.__fa(cu);
},
_removeAll:function(){if(!this.__eV){return;
}var ds=this.__eV.concat();
this.__eV.length=0;
for(var cT=ds.length-1;cT>=0;cT--){ds[cT].setLayoutParent(null);
}qx.ui.core.queue.Layout.add(this);
},
_afterAddChild:null,
_afterRemoveChild:null,
__eY:function(cu,
du){{};
var ct=cu.getLayoutParent();
if(ct&&ct!=this){ct._remove(cu);
}cu.setLayoutParent(this);
if(du){cu.setLayoutProperties(du);
}else{this.updateLayoutProperties();
}this.__eW=null;
if(this._afterAddChild){this._afterAddChild(cu);
}},
__fa:function(cu){{};
cu.setLayoutParent(null);
if(this.__eR){this.__eR.invalidateChildrenCache();
}this.__eW=null;
qx.ui.core.queue.Layout.add(this);
if(this._afterRemoveChild){this._afterRemoveChild(cu);
}},
capture:function(){this.__eL.capture();
},
releaseCapture:function(){this.__eL.releaseCapture();
},
_applyPadding:function(dz,
dA,
dB){this.__eS=true;
qx.ui.core.queue.Layout.add(this);
},
_createProtectorElement:function(){if(this.__eQ){return;
}var dC=this.__eQ=new qx.html.Element;
{};
dC.setStyles({position:t,
top:0,
left:0,
zIndex:7});
if(qx.core.Variant.isSet(y,
M)){dC.setStyles({backgroundImage:bD+qx.util.ResourceManager.toUri(cq)+bE,
backgroundRepeat:bl});
}this.__eL.add(dC);
},
__fb:function(cK){var cr=new qx.html.Element;
cr.setStyles({position:t,
top:0,
left:0});
{};
cK.init(cr);
return cr;
},
_applyDecorator:function(dz,
dA){var cQ=qx.ui.core.Widget.__eN;
var cW=qx.theme.manager.Decoration.getInstance();
var cB=this.__eL;
var cS=this.__eO;
if(!this.__eQ){this._createProtectorElement();
}var dD;
if(dA){if(typeof dA===m){dD=dA.toHashCode();
}else{dD=dA;
dA=cW.resolve(dA);
}}var dE;
if(dz){if(typeof dz===m){dE=dz.toHashCode();
{};
}else{dE=dz;
dz=cW.resolve(dz);
}}if(dA){if(!cQ[dD]){cQ[dD]=[];
}cB.remove(cS);
cQ[dD].push(cS);
}if(dz){if(cQ[dE]&&cQ[dE].length>0){cS=cQ[dE].pop();
}else{cS=this.__fb(dz);
cS.setStyle(p,
5);
}var dF=this.getBackgroundColor();
dz.tint(cS,
dF);
cB.add(cS);
this.__eO=cS;
}else{delete this.__eO;
this._applyBackgroundColor(this.getBackgroundColor());
}if(dz&&!dA&&dF){this.getContainerElement().setStyle(u,
null);
}if(qx.ui.decoration.Util.insetsModified(dA,
dz)){this.__eS=true;
qx.ui.core.queue.Layout.add(this);
}else if(dz){var cV=this.getBounds();
if(cV){cW.resolve(dz).resize(cS,
cV.width,
cV.height);
}}},
_applyShadow:function(dz,
dA){var cQ=qx.ui.core.Widget.__eN;
var cW=qx.theme.manager.Decoration.getInstance();
var cB=this.__eL;
var dD;
if(dA){if(typeof dA===m){dD=dA.toHashCode();
}else{dD=dA;
dA=cW.resolve(dA);
}}var dE;
if(dz){if(typeof dz===m){dE=dz.toHashCode();
}else{dE=dz;
dz=cW.resolve(dz);
}}if(dA){if(!cQ[dD]){cQ[dD]=[];
}cB.remove(this.__eP);
cQ[dD].push(this.__eP);
}if(dz){var cS;
if(cQ[dE]&&cQ[dE].length>0){cS=cQ[dE].pop();
}else{cS=this.__fb(dz);
}cB.add(cS);
this.__eP=cS;
var cF=dz.getInsets();
cS.setStyles({left:(-cF.left)+a,
top:(-cF.top)+a});
var cV=this.getBounds();
if(cV){var cN=cV.width+cF.left+cF.right;
var cO=cV.height+cF.top+cF.bottom;
dz.resize(cS,
cN,
cO);
}}else{delete this.__eP;
}},
_applyTextColor:function(dz,
dA){},
_applyZIndex:function(dz,
dA){this.__eL.setStyle(p,
dz==null?0:dz);
},
_applyVisibility:function(dz,
dA){if(dz===f){this.__eL.show();
}else{this.__eL.hide();
}var ct=this.$$parent;
if(ct&&(dA==null||dz==null||dA===h||dz===h)){ct.invalidateLayoutChildren();
}},
_applyOpacity:function(dz,
dA){this.__eL.setStyle(bx,
dz);
},
_applyCursor:function(dz,
dA){if(dz==null&&!this.isSelectable()){dz=O;
}this.__eL.setStyle(bI,
dz);
},
_applyBackgroundColor:function(dz,
dA){var cK=this.getDecorator();
var cM=this.getShadow();
var dG=this.getBackgroundColor();
var cB=this.__eL;
if(cK||cM){var cS=this.__eO;
if(cS){var cL=qx.theme.manager.Decoration.getInstance().resolve(cK);
cL.tint(this.__eO,
dG);
}cB.setStyle(u,
null);
}else{var dH=qx.theme.manager.Color.getInstance().resolve(dG);
cB.setStyle(u,
dH);
}},
_applyFont:function(dz,
dA){},
hasState:function(dI){var dJ=this.__fc;
return dJ&&dJ[dI];
},
__fc:null,
addState:function(dI){var dJ=this.__fc;
if(!dJ){dJ=this.__fc={};
}
if(dJ[dI]){return;
}this.__fc[dI]=true;
if(dI===w){this.syncAppearance();
}else{qx.ui.core.queue.Appearance.add(this);
}var dK=this._forwardStates;
var dL=this.__ff;
if(dK&&dK[dI]&&dL){var dM;
for(var cX in dL){dM=dL[cX];
if(dM instanceof qx.ui.core.Widget){dL[cX].addState(dI);
}}}},
removeState:function(dI){var dJ=this.__fc;
if(!dJ||!dJ[dI]){return;
}delete this.__fc[dI];
if(dI===w){this.syncAppearance();
}else{qx.ui.core.queue.Appearance.add(this);
}var dK=this._forwardStates;
var dL=this.__ff;
if(dK&&dK[dI]&&dL){for(var cX in dL){var dM=dL[cX];
if(dM instanceof qx.ui.core.Widget){dM.removeState(dI);
}}}},
replaceState:function(dA,
dz){var dJ=this.__fc;
if(!dJ){dJ=this.__fc={};
}
if(!dJ[dz]){dJ[dz]=true;
}
if(dJ[dA]){delete dJ[dA];
}qx.ui.core.queue.Appearance.add(this);
var dK=this._forwardStates;
var dL=this.__ff;
if(dK&&dK[dz]&&dL){for(var cX in dL){var dM=dL[cX];
if(dM instanceof qx.ui.core.Widget){dM.replaceState(dA,
dz);
}}}},
__fd:null,
__fe:null,
syncAppearance:function(){var dJ=this.__fc;
var dN=this.__fd;
var cJ=qx.theme.manager.Appearance.getInstance();
var dO=qx.core.Property.$$method.setThemed;
var dP=qx.core.Property.$$method.resetThemed;
if(this.__fe){delete this.__fe;
if(dN){var dQ=cJ.styleFrom(dN,
dJ);
if(dQ){dN=null;
}}}if(!dN){var dR=this;
var cX=[];
do{cX.push(dR.$$subcontrol||dR.getAppearance());
}while(dR=dR.$$subparent);
dN=this.__fd=cX.reverse().join(cc);
}var dS=cJ.styleFrom(dN,
dJ);
if(dS){if(dQ){for(var dT in dQ){if(dS[dT]===undefined){this[dP[dT]]();
}}}var dT;
var dz;
var dU=cp;
for(var dT in dS){dz=dS[dT];
dz===dU?this[dP[dT]]():this[dO[dT]](dz);
}}else if(dQ){for(var dT in dQ){this[dP[dT]]();
}}},
_applyAppearance:function(dz,
dA){this.updateAppearance();
},
updateAppearance:function(){this.__fe=true;
qx.ui.core.queue.Appearance.add(this);
var dL=this.__ff;
if(dL){var dR;
for(var cX in dL){dR=dL[cX];
if(dR instanceof qx.ui.core.Widget){dR.updateAppearance();
}}}},
syncWidget:function(){},
getEventTarget:function(){var dV=this;
while(dV.getAnonymous()){dV=dV.getLayoutParent();
if(!dV){return null;
}}return dV;
},
getFocusTarget:function(){var dV=this;
if(!dV.getEnabled()){return null;
}
while(dV.getAnonymous()||!dV.getFocusable()){dV=dV.getLayoutParent();
if(!dV||!dV.getEnabled()){return null;
}}return dV;
},
getFocusElement:function(){return this.__eL;
},
isTabable:function(){return this.isFocusable();
},
_applyFocusable:function(dz,
dA){var dV=this.getFocusElement();
if(dz){var dW=this.getTabIndex();
if(dW==null){dW=1;
}dV.setAttribute(r,
dW);
if(qx.core.Variant.isSet(y,
M)){dV.setAttribute(by,
bT);
}else{dV.setStyle(bz,
N);
}}else{if(dV.isNativelyFocusable()){dV.setAttribute(r,
-1);
}else if(dA){dV.setAttribute(r,
null);
}}},
_applyKeepFocus:function(dz){var dV=this.getFocusElement();
dV.setAttribute(co,
dz?k:null);
},
_applyKeepActive:function(dz){var dV=this.getContainerElement();
dV.setAttribute(cj,
dz?k:null);
},
_applyTabIndex:function(dz){if(dz==null){dz=1;
}else if(dz<1||dz>32000){throw new Error("TabIndex property must be between 1 and 32000");
}
if(this.getFocusable()&&dz!=null){this.getFocusElement().setAttribute(r,
dz);
}},
_applySelectable:function(dz){this._applyCursor(this.getCursor());
this.__eL.setAttribute(ce,
dz?k:bO);
if(qx.core.Variant.isSet(y,
cl)){this.__eL.setStyle(bq,
dz?bb:N);
}},
_applyEnabled:function(dz,
dA){if(dz===false){this.addState(C);
this.removeState(w);
if(this.isFocusable()){this.removeState(v);
this._applyFocusable(false,
true);
}}else{this.removeState(C);
if(this.isFocusable()){this._applyFocusable(true,
false);
}}},
_applyContextMenu:function(dz,
dA){if(dA){dA.removeState(s);
if(dA.getOpener()==this){dA.resetOpener();
}
if(!dz){this.removeListener(s,
this._onContextMenuOpen);
}}
if(dz){dz.setOpener(this);
dz.addState(s);
if(!dA){this.addListener(s,
this._onContextMenuOpen);
}}},
_onContextMenuOpen:function(dX){var dY=this.getContextMenu();
dY.placeToMouse(dX);
dY.show();
dX.preventDefault();
},
_onStopEvent:function(dX){dX.stopPropagation();
},
_applyDraggable:function(dz,
dA){qx.ui.core.DragDropCursor.getInstance();
if(dz){this.addListener(E,
this._onDragStart);
this.addListener(T,
this._onDrag);
this.addListener(H,
this._onDragEnd);
this.addListener(F,
this._onDragChange);
}else{this.removeListener(E,
this._onDragStart);
this.removeListener(T,
this._onDrag);
this.removeListener(H,
this._onDragEnd);
this.removeListener(F,
this._onDragChange);
}this.__eL.setAttribute(bm,
dz?k:null);
},
_applyDroppable:function(dz,
dA){this.__eL.setAttribute(bJ,
dz?k:null);
},
_onDragStart:function(dX){qx.ui.core.DragDropCursor.getInstance().placeToMouse(dX);
this.getApplicationRoot().setGlobalCursor(O);
},
_onDrag:function(dX){qx.ui.core.DragDropCursor.getInstance().placeToMouse(dX);
},
_onDragEnd:function(dX){qx.ui.core.DragDropCursor.getInstance().moveTo(-1000,
-1000);
this.getApplicationRoot().resetGlobalCursor();
},
_onDragChange:function(dX){var ea=qx.ui.core.DragDropCursor.getInstance();
var eb=dX.getCurrentAction();
eb?ea.setAction(eb):ea.resetAction();
},
visualizeFocus:function(){this.addState(v);
},
visualizeBlur:function(){this.removeState(v);
},
scrollChildIntoView:function(cu,
ec,
ed,
ee){this.scrollChildIntoViewX(cu,
ec,
ee);
this.scrollChildIntoViewY(cu,
ed,
ee);
},
scrollChildIntoViewX:function(cu,
ef,
ee){this.__eM.scrollChildIntoViewX(cu.getContainerElement(),
ef,
ee);
},
scrollChildIntoViewY:function(cu,
ef,
ee){this.__eM.scrollChildIntoViewY(cu.getContainerElement(),
ef,
ee);
},
focus:function(){if(this.isFocusable()){this.getFocusElement().focus();
}else{throw new Error("Widget is not focusable!");
}},
blur:function(){if(this.isFocusable()){this.getFocusElement().blur();
}else{throw new Error("Widget is not focusable!");
}},
activate:function(){this.__eL.activate();
},
deactivate:function(){this.__eL.deactivate();
},
tabFocus:function(){this.getFocusElement().focus();
},
_hasChildControl:function(cX){if(!this.__ff){return false;
}return !!this.__ff[cX];
},
__ff:null,
_getChildControl:function(cX,
eg){if(!this.__ff){if(eg){return null;
}this.__ff={};
}var dM=this.__ff[cX];
if(dM){return dM;
}
if(eg===true){return null;
}return this._createChildControl(cX);
},
_showChildControl:function(cX){var dM=this._getChildControl(cX);
dM.show();
return dM;
},
_excludeChildControl:function(cX){var dM=this._getChildControl(cX,
true);
if(dM){dM.exclude();
}},
_isChildControlVisible:function(cX){var dM=this._getChildControl(cX,
true);
if(dM){return dM.isVisible();
}return false;
},
_createChildControl:function(cX){if(!this.__ff){this.__ff={};
}else if(this.__ff[cX]){throw new Error("Child control '"+cX+"' already created!");
}var dM=this._createChildControlImpl(cX);
if(!dM){throw new Error("Unsupported control: "+cX);
}dM.$$subcontrol=cX;
dM.$$subparent=this;
var dJ=this.__fc;
var dK=this._forwardStates;
if(dJ&&dK&&dM instanceof qx.ui.core.Widget){for(var dI in dJ){if(dK[dI]){dM.addState(dI);
}}}return this.__ff[cX]=dM;
},
_createChildControlImpl:function(cX){return null;
},
_disposeChildControls:function(){var dL=this.__ff;
if(!dL){return;
}var eh=qx.ui.core.Widget;
for(var cX in dL){var dM=dL[cX];
if(!eh.contains(this,
dM)){dM.destroy();
}else{dM.dispose();
}}delete this.__ff;
},
_findTopControl:function(){var dR=this;
while(dR){if(!dR.$$subparent){return dR;
}dR=dR.$$subparent;
}return null;
},
getContainerLocation:function(ei){var ej=this.getContainerElement().getDomElement();
return ej?qx.bom.element.Location.get(ej,
ei):null;
},
getContentLocation:function(ei){var ej=this.getContentElement().getDomElement();
return ej?qx.bom.element.Location.get(ej,
ei):null;
},
setDomLeft:function(dz){var ej=this.getContainerElement().getDomElement();
if(ej){ej.style.left=dz+a;
}else{throw new Error("DOM element is not yet created!");
}},
setDomTop:function(dz){var ej=this.getContainerElement().getDomElement();
if(ej){ej.style.top=dz+a;
}else{throw new Error("DOM element is not yet created!");
}},
setDomPosition:function(cw,
cx){var ej=this.getContainerElement().getDomElement();
if(ej){ej.style.left=cw+a;
ej.style.top=cx+a;
}else{throw new Error("DOM element is not yet created!");
}},
destroy:function(){if(this.$$disposed){return;
}var ct=this.getLayoutParent();
if(ct){ct._remove(this);
}qx.ui.core.queue.Dispose.add(this);
},
clone:function(){var ek=arguments.callee.base.call(this);
if(this.getChildren){var ds=this.getChildren();
for(var cT=0,
cU=ds.length;cT<cU;cT++){ek.add(ds[cT].clone());
}}return ek;
},
serialize:function(){var em=arguments.callee.base.call(this);
if(this.getChildren){var ds=this.getChildren();
if(ds.length>0){em.children=[];
for(var cT=0,
cU=ds.length;cT<cU;cT++){em.children.push(ds[cT].serialize());
}}}
if(this.getLayout){var cv=this.getLayout();
if(cv){em.layout=cv.serialize();
}}return em;
}},
destruct:function(){if(!qx.core.ObjectRegistry.inShutDown){this.__eL.setAttribute(L,
null,
true);
this._disposeChildControls();
}this._disposeArray(bV);
this._disposeArray(ch);
this._disposeArray(U);
this._disposeFields(bc);
this._disposeObjects(bn,
cf,
bX,
ba,
bG,
be);
}});
})();
(function(){var a="100%",
b="backgroundColor",
c="opacity",
d="_applyBlockerColor",
e="__fj",
f="Number",
g="zIndex",
h="qx.ui.core.MBlocker",
j="_applyBlockerOpacity",
k="Color",
l="absolute";
qx.Mixin.define(h,
{properties:{blockerColor:{check:k,
init:null,
nullable:true,
apply:d,
themeable:true},
blockerOpacity:{check:f,
init:1,
apply:j,
themeable:true}},
members:{__fg:null,
__fh:null,
__fi:null,
__fj:null,
__fk:null,
_applyBlockerColor:function(m,
n){var o=[];
this.__fg&&o.push(this.__fg);
this.__fj&&o.push(this.__fj);
for(var p=0;p<o.length;p++){o[p].setStyle(b,
qx.theme.manager.Color.getInstance().resolve(m));
}},
_applyBlockerOpacity:function(m,
n){var o=[];
this.__fg&&o.push(this.__fg);
this.__fj&&o.push(this.__fj);
for(var p=0;p<o.length;p++){o[p].setStyle(c,
m);
}},
__fl:function(){var q=new qx.html.Element().setStyles({position:l,
width:a,
height:a,
opacity:this.getBlockerOpacity(),
backgroundColor:qx.theme.manager.Color.getInstance().resolve(this.getBlockerColor())});
return q;
},
_getBlocker:function(){if(!this.__fg){this.__fg=this.__fl();
this.getContentElement().add(this.__fg);
this.__fg.exclude();
}return this.__fg;
},
block:function(){if(this.__fh){return;
}this.__fh=true;
this._getBlocker().include();
this.__fi=this.getAnonymous();
this.setAnonymous(true);
},
isBlocked:function(){return !!this.__fh;
},
unblock:function(){if(!this.__fh){return;
}this.__fh=false;
this.setAnonymous(this.__fi);
this._getBlocker().exclude();
},
_getContentBlocker:function(){if(!this.__fj){this.__fj=this.__fl();
this.getContentElement().add(this.__fj);
this.__fj.exclude();
}return this.__fj;
},
blockContent:function(r){var q=this._getContentBlocker();
q.setStyle(g,
r);
if(this.__fk){return;
}this.__fk=true;
q.include();
},
isContentBlocked:function(){return !!this.__fk;
},
unblockContent:function(){if(!this.__fk){return;
}this.__fk=false;
this._getContentBlocker().exclude();
}},
destruct:function(){this._disposeObjects(e);
}});
})();
(function(){var a="qx.ui.window.Window",
b="changeModal",
c="changeVisibility",
d="changeActive",
f="_applyActiveWindow",
g="__fm",
h="__fn",
i="qx.ui.window.MDesktop";
qx.Mixin.define(i,
{properties:{activeWindow:{check:a,
apply:f}},
members:{__fm:null,
__fn:null,
getWindowManager:function(){if(!this.__fn){this.setWindowManager(new qx.ui.window.Window.DEFAULT_MANAGER_CLASS());
}return this.__fn;
},
supportsMaximize:function(){return true;
},
setWindowManager:function(j){if(this.__fn){this.__fn.setDesktop(null);
}j.setDesktop(this);
this.__fn=j;
},
_onChangeActive:function(k){if(k.getData()){this.setActiveWindow(k.getTarget());
}},
_applyActiveWindow:function(l,
m){this.getWindowManager().changeActiveWindow(l,
m);
l.setActive(true);
if(m){m.resetActive();
}},
_onChangeModal:function(k){this.getWindowManager().updateStack();
},
_onChangeVisibility:function(){this.getWindowManager().updateStack();
},
_afterAddChild:function(n){if(qx.Class.isDefined(a)&&n instanceof qx.ui.window.Window){this._addWindow(n);
}},
_addWindow:function(n){this.getWindows().push(n);
n.addListener(d,
this._onChangeActive,
this);
n.addListener(b,
this._onChangeModal,
this);
n.addListener(c,
this._onChangeVisibility,
this);
if(n.getActive()){this.setActiveWindow(n);
}this.getWindowManager().updateStack();
},
_afterRemoveChild:function(n){if(qx.Class.isDefined(a)&&n instanceof qx.ui.window.Window){this._removeWindow(n);
}},
_removeWindow:function(n){qx.lang.Array.remove(this.getWindows(),
n);
n.removeListener(d,
this._onChangeActive,
this);
n.removeListener(b,
this._onChangeModal,
this);
n.removeListener(c,
this._onChangeVisibility,
this);
this.getWindowManager().updateStack();
},
getWindows:function(){if(!this.__fm){this.__fm=[];
}return this.__fm;
}},
destruct:function(){this._disposeArray(g);
this._disposeObjects(h);
}});
})();
(function(){var a="contextmenu",
b="changeGlobalCursor",
c="abstract",
d="Boolean",
f="root",
g="__fo",
h="",
i="_applyNativeContextMenu",
j=" !important",
k="_applyGlobalCursor",
l="qx.client",
m=";",
n="qx.ui.root.Abstract",
o="String",
p="*";
qx.Class.define(n,
{type:c,
extend:qx.ui.core.Widget,
include:[qx.ui.core.MChildrenHandling,
qx.ui.core.MBlocker,
qx.ui.window.MDesktop],
construct:function(){arguments.callee.base.call(this);
qx.ui.core.FocusHandler.getInstance().addRoot(this);
},
properties:{appearance:{refine:true,
init:f},
enabled:{refine:true,
init:true},
focusable:{refine:true,
init:true},
globalCursor:{check:o,
nullable:true,
themeable:true,
apply:k,
event:b},
nativeContextMenu:{check:d,
nullable:true,
apply:i,
init:true}},
members:{isRootWidget:function(){return true;
},
getLayout:function(){return this._getLayout();
},
_applyGlobalCursor:qx.core.Variant.select(l,
{"mshtml":function(q,
r){},
"default":function(q,
r){var s=qx.bom.Stylesheet;
var t=this.__fo;
if(!t){this.__fo=t=s.createElement();
}s.removeAllRules(t);
if(q){s.addRule(t,
p,
qx.bom.element.Cursor.compile(q).replace(m,
h)+j);
}}}),
_applyNativeContextMenu:function(q,
r){if(q){this.removeListener(a,
this._onNativeContextMenu,
this,
true);
}else{this.addListener(a,
this._onNativeContextMenu,
this,
true);
}},
_onNativeContextMenu:function(u){u.preventDefault();
}},
destruct:function(){this._disposeFields(g);
},
defer:function(v,
w){qx.ui.core.MChildrenHandling.remap(w);
}});
})();
(function(){var a="resize",
b="position",
c="_doc",
d="0px",
f="qx.ui.root.Application",
g="hidden",
h="div",
i="_window",
j="100%",
k="absolute";
qx.Class.define(f,
{extend:qx.ui.root.Abstract,
construct:function(l){this._window=qx.dom.Node.getWindow(l);
this._doc=l;
arguments.callee.base.call(this);
qx.event.Registration.addListener(this._window,
a,
this._onResize,
this);
this._setLayout(new qx.ui.layout.Canvas());
qx.ui.core.queue.Layout.add(this);
qx.ui.core.FocusHandler.getInstance().connectTo(this);
},
members:{_createContainerElement:function(){var l=this._doc;
var m=l.documentElement.style;
var n=l.body.style;
m.overflow=n.overflow=g;
m.padding=m.margin=n.padding=n.margin=d;
m.width=m.height=n.width=n.height=j;
var o=l.createElement(h);
l.body.appendChild(o);
var p=new qx.html.Root(o);
p.setStyle(b,
k);
return p;
},
_onResize:function(q){qx.ui.core.queue.Layout.add(this);
},
_computeSizeHint:function(){var r=qx.bom.Viewport.getWidth(this._window);
var s=qx.bom.Viewport.getHeight(this._window);
return {minWidth:r,
width:r,
maxWidth:r,
minHeight:s,
height:s,
maxHeight:s};
}},
destruct:function(){this._disposeFields(i,
c);
}});
})();
(function(){var a="blur",
b="focus",
c="load",
d="input",
e="qx.ui.core.EventHandler",
f="__fp";
qx.Class.define(e,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(){arguments.callee.base.call(this);
this.__fp=qx.event.Registration.getManager();
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_FIRST,
SUPPORTED_TYPES:{mousemove:1,
mouseover:1,
mouseout:1,
mousedown:1,
mouseup:1,
click:1,
dblclick:1,
contextmenu:1,
mousewheel:1,
keyup:1,
keydown:1,
keypress:1,
keyinput:1,
capture:1,
losecapture:1,
focusin:1,
focusout:1,
focus:1,
blur:1,
activate:1,
deactivate:1,
appear:1,
disappear:1,
dragstart:1,
dragend:1,
dragover:1,
dragleave:1,
drop:1,
drag:1,
dragchange:1,
droprequest:1},
IGNORE_CAN_HANDLE:false},
members:{__fp:null,
__fq:{focusin:1,
focusout:1,
focus:1,
blur:1},
__fr:{mouseover:1,
mouseout:1,
appear:1,
disappear:1},
canHandleEvent:function(g,
h){return g instanceof qx.ui.core.Widget;
},
_dispatchEvent:function(j){var k=j.getTarget();
var m=qx.ui.core.Widget.getWidgetByElement(k,
true);
while(m&&m.isAnonymous()){m=m.getLayoutParent();
}
if(!m){return;
}if(this.__fq[j.getType()]){m=m.getFocusTarget();
if(!m){return;
}}if(j.getRelatedTarget){var n=j.getRelatedTarget();
var o=qx.ui.core.Widget.getWidgetByElement(n);
while(o&&o.isAnonymous()){o=o.getLayoutParent();
}
if(o){if(this.__fq[j.getType()]){o=o.getFocusTarget();
}if(o===m){return;
}}}var p=j.getCurrentTarget();
var q=qx.ui.core.Widget.getWidgetByElement(p);
if(!q||q.isAnonymous()){return;
}if(this.__fq[j.getType()]){q=q.getFocusTarget();
}var h=j.getType();
if(!(q.isEnabled()||this.__fr[h])){return;
}var r=j.getEventPhase()==qx.event.type.Event.CAPTURING_PHASE;
var s=this.__fp.getListeners(q,
h,
r);
if(!s||s.length===0){return;
}var t=qx.event.Pool.getInstance().getObject(j.constructor);
j.clone(t);
t.setTarget(m);
t.setRelatedTarget(o||null);
t.setCurrentTarget(m);
var u=j.getOriginalTarget();
if(u){var v=qx.ui.core.Widget.getWidgetByElement(u);
while(v&&v.isAnonymous()){v=v.getLayoutParent();
}t.setOriginalTarget(v);
}else{t.setOriginalTarget(k);
}for(var w=0,
x=s.length;w<x;w++){var y=s[w].context||q;
s[w].handler.call(y,
t);
}if(t.getPropagationStopped()){j.stopPropagation();
}
if(t.getDefaultPrevented()){j.preventDefault();
}qx.event.Pool.getInstance().poolObject(t);
},
registerEvent:function(g,
h,
r){var z;
if(h===b||h===a){z=g.getFocusElement();
}else if(h===c||h===d){z=g.getContentElement();
}else{z=g.getContainerElement();
}
if(z){z.addListener(h,
this._dispatchEvent,
this,
r);
}},
unregisterEvent:function(g,
h,
r){var z;
if(h===b||h===a){z=g.getFocusElement();
}else if(h===c||h===d){z=g.getContentElement();
}else{z=g.getContainerElement();
}
if(z){z.removeListener(h,
this._dispatchEvent,
this,
r);
}}},
destruct:function(){this._disposeFields(f);
},
defer:function(A){qx.event.Registration.addHandler(A);
}});
})();
(function(){var a="/",
b="qx.util.ResourceManager",
c="string";
qx.Bootstrap.define(b,
{statics:{__fs:window.qxresources||{},
has:function(d){return !!this.__fs[d];
},
getData:function(d){return this.__fs[d]||null;
},
getImageWidth:function(d){var e=this.__fs[d];
return e?e[0]:null;
},
getImageHeight:function(d){var e=this.__fs[d];
return e?e[1]:null;
},
getImageFormat:function(d){var e=this.__fs[d];
return e?e[2]:null;
},
isClippedImage:function(d){var e=this.__fs[d];
return e&&e.length>4;
},
toUri:function(d){if(d==null){return d;
}var e=this.__fs[d];
if(!e){return d;
}
if(typeof e===c){var f=e;
}else{var f=e[3];
if(!f){return d;
}}return window.qxlibraries[f].resourceUri+a+d;
}}});
})();
(function(){var c='<div style="',
d='"></div>',
e='"/>',
f="",
g='" style="vertical-align:top;',
h="scale",
i="qx.ui.decoration.Util",
j='<img src="';
qx.Class.define(i,
{statics:{insetsModified:function(k,
l){if(k==l){return false;
}
if(k==null||l==null){return true;
}var m=qx.theme.manager.Decoration.getInstance();
var n=m.resolve(k).getInsets();
var o=m.resolve(l).getInsets();
if(n.top!=o.top||n.right!=o.right||n.bottom!=o.bottom||n.left!=o.left){return true;
}return false;
},
generateBackgroundMarkup:function(p,
q,
r){if(p){var s=qx.util.AliasManager.getInstance().resolve(p);
if(q==h){var t=qx.util.ResourceManager.toUri(s);
return j+t+g+r+e;
}else{var u=qx.bom.element.Background.compile(s,
q,
0,
0);
return c+u+r+d;
}}else{if(r){return c+r+d;
}else{return f;
}}}}});
})();
(function(){var a="px",
b="0px",
c="qx.client",
d="/",
e="mshtml",
f="",
g=" ",
h=";",
i="background-image:url(",
j=");",
k="0 0",
l="url(",
m=")",
n="background-repeat:",
o="qx.bom.element.Background",
p="background-position:",
q="https:";
qx.Class.define(o,
{statics:{__ft:[i,
null,
j,
p,
null,
h,
n,
null,
h],
__fu:{backgroundImage:null,
backgroundPosition:null,
backgroundRepeat:null},
compile:function(r,
s,
t,
u){var v=qx.bom.client.Engine;
if(v.GECKO&&v.VERSION<1.9&&t==u&&t!=null){u+=0.01;
}
if(t!=null||u!=null){var w=(t==null?b:t+a)+g+(u==null?b:u+a);
}else{var w=k;
}var x=qx.util.ResourceManager.toUri(r);
if(qx.core.Variant.isSet(c,
e)){x=this.__fv(x);
}var y=this.__ft;
y[1]=x;
y[4]=w;
y[7]=s;
return y.join(f);
},
getStyles:function(r,
s,
t,
u){if(!r){return this.__fu;
}var v=qx.bom.client.Engine;
if(v.GECKO&&v.VERSION<1.9&&t==u&&t!=null){u+=0.01;
}
if(t!=null||u!=null){var w=(t==null?b:t+a)+g+(u==null?b:u+a);
}var x=qx.util.ResourceManager.toUri(r);
if(qx.core.Variant.isSet(c,
e)){x=this.__fv(x);
}var z={backgroundImage:l+x+m};
if(w!=null){z.backgroundPosition=w;
}
if(s!=null){z.backgroundRepeat=s;
}return z;
},
set:function(A,
r,
s,
t,
u){var B=this.getStyles(r,
s,
t,
u);
for(var C in B){A.style[C]=B[C];
}},
__fv:qx.core.Variant.select(c,
{"mshtml":function(D){var E=f;
if(window.location.protocol===q){if(D.match(/^\/\//)!=null){E=window.location.protocol;
}else if(D.match(/^\.\//)!=null){D=D.substring(D.indexOf(d));
E=document.URL.substring(0,
document.URL.lastIndexOf(d));
}else{E=window.location.href.substring(0,
window.location.href.lastIndexOf(d)+1);
}}return E+D;
},
"default":function(){}})}});
})();
(function(){var a="replacement",
b="Boolean",
c="_applyScale",
d="_applySource",
e="-disabled.$1",
f="changeSource",
g="String",
h="image",
i="qx.ui.basic.Image";
qx.Class.define(i,
{extend:qx.ui.core.Widget,
construct:function(j){arguments.callee.base.call(this);
if(j){this.setSource(j);
}},
properties:{source:{check:g,
init:null,
nullable:true,
event:f,
apply:d,
themeable:true},
scale:{check:b,
init:false,
themeable:true,
apply:c},
appearance:{refine:true,
init:h},
allowShrinkX:{refine:true,
init:false},
allowShrinkY:{refine:true,
init:false},
allowGrowX:{refine:true,
init:false},
allowGrowY:{refine:true,
init:false}},
members:{__fw:null,
__fx:null,
_createContentElement:function(){return new qx.html.Image();
},
_getContentHint:function(){return {width:this.__fw||0,
height:this.__fx||0};
},
_applyEnabled:function(k,
l){arguments.callee.base.call(this,
k,
l);
if(this.getSource()){this._styleSource();
}},
_applySource:function(k){this._styleSource();
},
_applyScale:function(k){var m=this.getContentElement();
m.setScale(k);
},
_styleSource:function(){var j=qx.util.AliasManager.getInstance().resolve(this.getSource());
var m=this.getContentElement();
if(!j){m.resetSource();
return;
}var n=qx.util.ResourceManager;
var o=qx.io2.ImageLoader;
if(n.has(j)){if(!this.getEnabled()){var p=j.replace(/\.([a-z]+)$/,
e);
if(n.has(p)){j=p;
this.addState(a);
}else{this.removeState(a);
}}if(m.getSource()===j){return;
}m.setSource(j);
this._updateSize(n.getImageWidth(j),
n.getImageHeight(j));
}else if(o.isLoaded(j)){m.setSource(j);
var q=o.getWidth(j);
var r=o.getHeight(j);
this._updateSize(q,
r);
}else{var s;
if(!qx.io2.ImageLoader.isFailed(j)){qx.io2.ImageLoader.load(j,
this.__fy,
this);
}}},
__fy:function(j,
t){if(j!==qx.util.AliasManager.getInstance().resolve(this.getSource())){return;
}if(!t){this.warn("Image could not be loaded: "+j);
return;
}this._styleSource();
},
_updateSize:function(q,
r){if(q!==this.__fw||r!==this.__fx){this.__fw=q;
this.__fx=r;
qx.ui.core.queue.Layout.add(this);
}}}});
})();
(function(){var a="Integer",
b="Boolean",
c="bottom-left",
d="offsetLeft",
e="offsetRight",
f="right-top",
g="top-right",
h="top-left",
i="bottom-right",
j="right-bottom",
k="offsetBottom",
l="qx.ui.core.MPlacement",
m="left-top",
n="left-bottom",
o="shorthand",
p="offsetTop";
qx.Mixin.define(l,
{properties:{position:{check:[h,
g,
c,
i,
m,
n,
f,
j],
init:c,
themeable:true},
domMove:{check:b,
init:false},
smart:{check:b,
init:true,
themeable:true},
offsetLeft:{check:a,
init:0,
themeable:true},
offsetTop:{check:a,
init:0,
themeable:true},
offsetRight:{check:a,
init:0,
themeable:true},
offsetBottom:{check:a,
init:0,
themeable:true},
offset:{group:[p,
e,
k,
d],
mode:o,
themeable:true}},
members:{getLayoutLocation:function(q){var r,
s,
t,
u;
s=q.getBounds();
t=s.left;
u=s.top;
var v=s;
q=q.getLayoutParent();
while(q&&!q.isRootWidget()){s=q.getBounds();
t+=s.left;
u+=s.top;
r=q.getInsets();
t+=r.left;
u+=r.top;
q=q.getLayoutParent();
}if(q.isRootWidget()){var w=q.getContainerLocation();
if(w){t+=w.left;
u+=w.top;
}}return {left:t,
top:u,
right:t+v.width,
bottom:u+v.height};
},
moveTo:function(t,
u){if(this.getDomMove()){this.setDomPosition(t,
u);
}else{this.setLayoutProperties({left:t,
top:u});
}},
placeToWidget:function(x){var y=x.getContainerLocation()||this.getLayoutLocation(x);
this.__fz(y);
},
placeToMouse:function(z){var t=z.getDocumentLeft();
var u=z.getDocumentTop();
var y={left:t,
top:u,
right:t,
bottom:u};
this.__fz(y);
},
placeToElement:function(A){var B=qx.bom.element.Location.get(A);
var y={left:B.left,
top:B.top,
right:B.left+A.offsetWidth,
bottom:B.top+A.offsetHeight};
this.__fz(y);
},
placeToPoint:function(C){var y={left:C.left,
top:C.top,
right:C.left,
bottom:C.top};
this.__fz(y);
},
__fz:function(y){var v=this.getSizeHint();
var D=this.getLayoutParent().getBounds();
var E=this.getPosition();
var F=this.getSmart();
var G={left:this.getOffsetLeft(),
top:this.getOffsetTop(),
right:this.getOffsetRight(),
bottom:this.getOffsetBottom()};
var H=qx.util.PlaceUtil.compute(v,
D,
y,
E,
F,
G);
this.moveTo(H.left,
H.top);
}}});
})();
(function(){var a="dragdrop-cursor",
b="_applyAction",
c="alias",
d="qx.ui.core.DragDropCursor",
e="move",
f="singleton",
g="copy";
qx.Class.define(d,
{extend:qx.ui.basic.Image,
include:qx.ui.core.MPlacement,
type:f,
construct:function(){arguments.callee.base.call(this);
this.setZIndex(1e8);
this.setDomMove(true);
var h=this.getApplicationRoot();
h.add(this,
{left:-1000,
top:-1000});
},
properties:{appearance:{refine:true,
init:a},
action:{check:[c,
g,
e],
apply:b,
nullable:true}},
members:{_applyAction:function(i,
j){if(j){this.removeState(j);
}
if(i){this.addState(i);
}}}});
})();
(function(){var a="scale",
b="source",
c="no-repeat",
d="qx.html.Image";
qx.Class.define(d,
{extend:qx.html.Element,
members:{_applyProperty:function(e,
f){arguments.callee.base.call(this,
e,
f);
if(e===b){var g=this._element;
var h=this._getProperty(b);
var i=this._getProperty(a);
var j=i?a:c;
qx.bom.element.Decoration.update(g,
h,
j);
}},
_createDomElement:function(){var i=this._getProperty(a);
var j=i?a:c;
this._nodeName=qx.bom.element.Decoration.getTagName(j);
return arguments.callee.base.call(this);
},
_copyData:function(k){return arguments.callee.base.call(this,
true);
},
setSource:function(f){this._setProperty(b,
f);
return this;
},
getSource:function(){return this._getProperty(b);
},
resetSource:function(){this._removeProperty(b);
return this;
},
setScale:function(f){this._setProperty(a,
f);
return this;
},
getScale:function(){return this._getProperty(a);
}}});
})();
(function(){var a="px",
b="qx.client",
c="div",
d="img",
e="scale-x",
f="",
g="mshtml",
h="no-repeat",
i="scale-y",
j="repeat",
k="scale",
l="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='",
m='<div style="',
n="repeat-y",
o='<img src="',
p="qx.bom.element.Decoration",
q="png",
r="', sizingMethod='scale')",
s='"/>',
t='" style="',
u="none",
v="webkit",
w="repeat-x",
x='"></div>',
y="absolute";
qx.Class.define(p,
{statics:{DEBUG:false,
__fA:qx.core.Variant.isSet(b,
g)&&qx.bom.client.Engine.VERSION<8,
__fB:qx.core.Variant.select(b,
{"mshtml":{"scale-x":true,
"scale-y":true,
"scale":true,
"no-repeat":true},
"default":null}),
__fC:{"scale-x":d,
"scale-y":d,
"scale":d,
"repeat":c,
"no-repeat":c,
"repeat-x":c,
"repeat-y":c},
update:function(z,
A,
B,
C){var D=this.getTagName(B);
if(D!=z.tagName.toLowerCase()){throw new Error("Image modification not possible because elements could not be replaced at runtime anymore!");
}var E=this.getAttributes(A,
B,
C);
if(D===d){z.src=E.src;
}if(z.style.backgroundPosition!=f&&E.style.backgroundPosition===undefined){E.style.backgroundPosition=null;
}if(z.style.clip!=f&&E.style.clip===undefined){E.style.clip=null;
}var F=qx.bom.element.Style;
F.setStyles(z,
E.style);
},
create:function(A,
B,
C){var D=this.getTagName(B);
var E=this.getAttributes(A,
B,
C);
var G=qx.bom.element.Style.compile(E.style);
if(D===d){return o+E.src+t+G+s;
}else{return m+G+x;
}},
getTagName:function(B){if(qx.core.Variant.isSet(b,
g)){if(this.__fA&&this.__fB[B]){return c;
}}return this.__fC[B];
},
getAttributes:function(A,
B,
C){var H=qx.util.ResourceManager;
var I=qx.io2.ImageLoader;
var J=qx.bom.element.Background;
if(!C){C={};
}
if(!C.position){C.position=y;
}
if(qx.core.Variant.isSet(b,
g)){C.fontSize=0;
C.lineHeight=0;
}else if(qx.core.Variant.isSet(b,
v)){C.WebkitUserDrag=u;
}var K=H.getImageWidth(A)||I.getWidth(A);
var L=H.getImageHeight(A)||I.getHeight(A);
var M=H.getImageFormat(A);
if(this.__fA&&this.__fB[B]&&M===q){if(C.width==null){C.width=K==null?K:K+a;
}
if(C.height==null){C.height=L==null?L:L+a;
}C.filter=l+H.toUri(A)+r;
C.backgroundImage=C.backgroundRepeat=f;
return {style:C};
}else{var N=H.isClippedImage(A);
if(B===k){var O=H.toUri(A);
if(!C.width){C.width=K==null?K:K+a;
}
if(!C.height){C.height=L==null?L:L+a;
}return {src:O,
style:C};
}else if(B===e||B===i){if(N){if(B===e){var P=H.getData(A);
var Q=H.getImageHeight(P[4]);
var O=H.toUri(P[4]);
C.clip={top:-P[6],
height:L};
C.height=Q+a;
if(C.top!=null){C.top=(parseInt(C.top)+P[6])+a;
}else if(C.bottom!=null){C.bottom=(parseInt(C.bottom)+L-Q-P[6])+a;
}return {src:O,
style:C};
}else{var P=H.getData(A);
var R=H.getImageWidth(P[4]);
var O=H.toUri(P[4]);
C.clip={left:-P[5],
width:K};
C.width=R+a;
if(C.left!=null){C.left=(parseInt(C.left)+P[5])+a;
}else if(C.right!=null){C.right=(parseInt(C.right)+K-R-P[5])+a;
}return {src:O,
style:C};
}}else{{};
if(B==e){C.height=L==null?null:L+a;
}else if(B==i){C.width=K==null?null:K+a;
}var O=H.toUri(A);
return {src:O,
style:C};
}}else{if(N&&B!==j){var P=H.getData(A);
var S=J.getStyles(P[4],
B,
P[5],
P[6]);
for(var T in S){C[T]=S[T];
}
if(B==n||B===h){C.width=K==null?K:K+a;
}
if(B==w||B===h){C.height=L==null?L:L+a;
}return {style:C};
}else{{};
var S=J.getStyles(A,
B);
for(var T in S){C[T]=S[T];
}C.width=K==null?K:K+a;
C.height=L==null?L:L+a;
return {style:C};
}}}}}});
})();
(function(){var a="qx.client",
b="qx.io2.ImageLoader",
c="load";
qx.Bootstrap.define(b,
{statics:{__fD:{},
__fE:{width:null,
height:null},
isLoaded:function(d){var e=this.__fD[d];
return !!(e&&e.loaded);
},
isFailed:function(d){var e=this.__fD[d];
return !!(e&&e.failed);
},
isLoading:function(d){var e=this.__fD[d];
return !!(e&&e.loading);
},
getSize:function(d){return this.__fD[d]||this.__fE;
},
getWidth:function(d){var e=this.__fD[d];
return e?e.width:null;
},
getHeight:function(d){var e=this.__fD[d];
return e?e.height:null;
},
load:function(d,
f,
g){var e=this.__fD[d];
if(!e){e=this.__fD[d]={};
}if(f&&!g){g=window;
}if(e.loaded||e.loading||e.failed){if(f){if(e.loading){e.callbacks.push(f,
g);
}else{f.call(g,
d,
e);
}}}else{e.loading=true;
e.callbacks=[];
if(f){e.callbacks.push(f,
g);
}var h=new Image();
var j=qx.lang.Function.listener(this.__fF,
this,
h,
d);
h.onload=j;
h.onerror=j;
h.src=d;
}},
__fF:function(k,
m,
d){var e=this.__fD[d];
if(k.type===c){e.loaded=true;
e.width=this.__fG(m);
e.height=this.__fH(m);
}else{e.failed=true;
}m.onload=m.onerror=null;
var n=e.callbacks;
delete e.loading;
delete e.callbacks;
for(var o=0,
p=n.length;o<p;o+=2){n[o].call(n[o+1],
d,
e);
}},
__fG:qx.core.Variant.select(a,
{"gecko":function(m){return m.naturalWidth;
},
"default":function(m){return m.width;
}}),
__fH:qx.core.Variant.select(a,
{"gecko":function(m){return m.naturalHeight;
},
"default":function(m){return m.height;
}})}});
})();
(function(){var a="bottom",
b="top",
c="left",
d="right",
e="-",
f="qx.util.PlaceUtil";
qx.Class.define(f,
{statics:{compute:function(g,
h,
i,
j,
k,
l){var m=0;
var n=0;
var o,
p;
var q=j.split(e);
var r=q[0];
var s=q[1];
var t=0,
u=0,
v=0,
w=0;
if(l){t+=l.left||0;
u+=l.top||0;
v+=l.right||0;
w+=l.bottom||0;
}switch(r){case c:m=i.left-g.width-t;
break;
case b:n=i.top-g.height-u;
break;
case d:m=i.right+v;
break;
case a:n=i.bottom+w;
break;
}switch(s){case c:m=i.left;
break;
case b:n=i.top;
break;
case d:m=i.right-g.width;
break;
case a:n=i.bottom-g.height;
break;
}
if(k===false){return {left:m,
top:n};
}else{var x=Math.min(m,
h.width-m-g.width);
if(x<0){var y=m;
if(m<0){if(r==c){y=i.right+v;
}else if(s==d){y=i.left;
}}else{if(r==d){y=i.left-g.width-t;
}else if(s==c){y=i.right-g.width;
}}o=Math.min(y,
h.width-y-g.width);
if(o>x){m=y;
x=o;
}}var z=Math.min(n,
h.height-n-g.height);
if(z<0){var A=n;
if(n<0){if(r==b){A=i.bottom+w;
}else if(s==a){A=i.top;
}}else{if(r==a){A=i.top-g.height-u;
}else if(s==b){A=i.bottom-g.height;
}}p=Math.min(A,
h.height-A-g.height);
if(p>z){n=A;
z=p;
}}return {left:m,
top:n,
ratingX:x,
ratingY:z};
}}}});
})();
(function(){var a="__fL",
b="keypress",
c="focusout",
d="__fJ",
f="__fK",
g="activate",
h="__fI",
j="Tab",
k="singleton",
m="deactivate",
n="focusin",
o="qx.ui.core.FocusHandler";
qx.Class.define(o,
{extend:qx.core.Object,
type:k,
construct:function(){arguments.callee.base.call(this);
this.__fI={};
},
members:{__fI:null,
__fJ:null,
__fK:null,
__fL:null,
connectTo:function(p){p.addListener(b,
this.__fM,
this);
p.addListener(n,
this._onFocusIn,
this,
true);
p.addListener(c,
this._onFocusOut,
this,
true);
p.addListener(g,
this._onActivate,
this,
true);
p.addListener(m,
this._onDeactivate,
this,
true);
},
addRoot:function(q){this.__fI[q.$$hash]=q;
},
removeRoot:function(q){delete this.__fI[q.$$hash];
},
isActive:function(q){return this.__fJ==q;
},
isFocused:function(q){return this.__fK==q;
},
isFocusRoot:function(q){!!this.__fI[q.$$hash];
},
_onActivate:function(r){var s=r.getTarget();
this.__fJ=s;
var p=this.__fN(s);
if(p!=this.__fL){this.__fL=p;
}},
_onDeactivate:function(r){var s=r.getTarget();
if(this.__fJ==s){this.__fJ=null;
}},
_onFocusIn:function(r){var s=r.getTarget();
if(s!=this.__fK){this.__fK=s;
s.visualizeFocus();
}},
_onFocusOut:function(r){var s=r.getTarget();
if(s==this.__fK){this.__fK=null;
s.visualizeBlur();
}},
__fM:function(r){if(r.getKeyIdentifier()!=j){return;
}
if(!this.__fL){return;
}r.stopPropagation();
r.preventDefault();
var t=this.__fK;
if(!r.isShiftPressed()){var u=t?this.__fR(t):this.__fP();
}else{var u=t?this.__fS(t):this.__fQ();
}if(u){u.tabFocus();
}},
__fN:function(q){var v=this.__fI;
while(q){if(v[q.$$hash]){return q;
}q=q.getLayoutParent();
}return null;
},
__fO:function(w,
x){if(w===x){return 0;
}var y=w.getTabIndex()||0;
var z=x.getTabIndex()||0;
if(y!=z){return y-z;
}var A=w.getContainerElement().getDomElement();
var B=x.getContainerElement().getDomElement();
var C=qx.bom.element.Location;
var D=C.get(A);
var E=C.get(B);
if(D.top!=E.top){return D.top-E.top;
}if(D.left!=E.left){return D.left-E.left;
}var F=w.getZIndex();
var G=x.getZIndex();
if(F!=G){return F-G;
}return 0;
},
__fP:function(){return this.__fV(this.__fL,
null);
},
__fQ:function(){return this.__fW(this.__fL,
null);
},
__fR:function(q){var p=this.__fL;
if(p==q){return this.__fP();
}
while(q&&q.getAnonymous()){q=q.getLayoutParent();
}
if(q==null){return [];
}var H=[];
this.__fT(p,
q,
H);
H.sort(this.__fO);
var I=H.length;
return I>0?H[0]:this.__fP();
},
__fS:function(q){var p=this.__fL;
if(p==q){return this.__fQ();
}
while(q&&q.getAnonymous()){q=q.getLayoutParent();
}
if(q==null){return [];
}var H=[];
this.__fU(p,
q,
H);
H.sort(this.__fO);
var I=H.length;
return I>0?H[I-1]:this.__fQ();
},
__fT:function(J,
q,
H){var K=J.getLayoutChildren();
var L;
for(var M=0,
N=K.length;M<N;M++){L=K[M];
if(!(L instanceof qx.ui.core.Widget)){continue;
}
if(!this.isFocusRoot(L)&&L.isEnabled()){if(L.isTabable()&&this.__fO(q,
L)<0){H.push(L);
}this.__fT(L,
q,
H);
}}},
__fU:function(J,
q,
H){var K=J.getLayoutChildren();
var L;
for(var M=0,
N=K.length;M<N;M++){L=K[M];
if(!(L instanceof qx.ui.core.Widget)){continue;
}
if(!this.isFocusRoot(L)&&L.isEnabled()){if(L.isTabable()&&this.__fO(q,
L)>0){H.push(L);
}this.__fU(L,
q,
H);
}}},
__fV:function(J,
O){var K=J.getLayoutChildren();
var L;
for(var M=0,
N=K.length;M<N;M++){L=K[M];
if(!(L instanceof qx.ui.core.Widget)){continue;
}if(!this.isFocusRoot(L)&&L.isEnabled()){if(L.isTabable()){if(O==null||this.__fO(L,
O)<0){O=L;
}}O=this.__fV(L,
O);
}}return O;
},
__fW:function(J,
P){var K=J.getLayoutChildren();
var L;
for(var M=0,
N=K.length;M<N;M++){L=K[M];
if(!(L instanceof qx.ui.core.Widget)){continue;
}if(!this.isFocusRoot(L)&&L.isEnabled()){if(L.isTabable()){if(P==null||this.__fO(L,
P)>0){P=L;
}}P=this.__fW(L,
P);
}}return P;
}},
destruct:function(){this._disposeMap(h);
this._disposeFields(f,
d,
a);
}});
})();
(function(){var a="qx.client",
b="head",
c="text/css",
d="stylesheet",
e="}",
f='@import "',
g="{",
h='";',
j="qx.bom.Stylesheet",
k="link",
l="style";
qx.Class.define(j,
{statics:{includeFile:function(m,
n){if(!n){n=document;
}var o=n.createElement(k);
o.type=c;
o.rel=d;
o.href=qx.util.ResourceManager.toUri(m);
var p=n.getElementsByTagName(b)[0];
p.appendChild(o);
},
createElement:qx.core.Variant.select(a,
{"mshtml":function(q){var r=document.createStyleSheet();
if(q){r.cssText=q;
}return r;
},
"default":function(q){var s=document.createElement(l);
s.type=c;
if(q){s.appendChild(document.createTextNode(q));
}document.getElementsByTagName(b)[0].appendChild(s);
return s.sheet;
}}),
addRule:qx.core.Variant.select(a,
{"mshtml":function(r,
t,
u){r.addRule(t,
u);
},
"default":function(r,
t,
u){r.insertRule(t+g+u+e,
r.cssRules.length);
}}),
removeRule:qx.core.Variant.select(a,
{"mshtml":function(r,
t){var v=r.rules;
var w=v.length;
for(var x=w-1;x>=0;--x){if(v[x].selectorText==t){r.removeRule(x);
}}},
"default":function(r,
t){var v=r.cssRules;
var w=v.length;
for(var x=w-1;x>=0;--x){if(v[x].selectorText==t){r.deleteRule(x);
}}}}),
removeAllRules:qx.core.Variant.select(a,
{"mshtml":function(r){var v=r.rules;
var w=v.length;
for(var x=w-1;x>=0;x--){r.removeRule(x);
}},
"default":function(r){var v=r.cssRules;
var w=v.length;
for(var x=w-1;x>=0;x--){r.deleteRule(x);
}}}),
addImport:qx.core.Variant.select(a,
{"mshtml":function(r,
y){r.addImport(y);
},
"default":function(r,
y){r.insertRule(f+y+h,
r.cssRules.length);
}}),
removeImport:qx.core.Variant.select(a,
{"mshtml":function(r,
y){var z=r.imports;
var w=z.length;
for(var x=w-1;x>=0;x--){if(z[x].href==y){r.removeImport(x);
}}},
"default":function(r,
y){var v=r.cssRules;
var w=v.length;
for(var x=w-1;x>=0;x--){if(v[x].href==y){r.deleteRule(x);
}}}}),
removeAllImports:qx.core.Variant.select(a,
{"mshtml":function(r){var z=r.imports;
var w=z.length;
for(var x=w-1;x>=0;x--){r.removeImport(x);
}},
"default":function(r){var v=r.cssRules;
var w=v.length;
for(var x=w-1;x>=0;x--){if(v[x].type==v[x].IMPORT_RULE){r.deleteRule(x);
}}}})}});
})();
(function(){var a="abstract",
b="qx.ui.layout.Abstract",
c="__fX",
d="__fY";
qx.Class.define(b,
{type:a,
extend:qx.core.Object,
members:{__fX:null,
_invalidChildrenCache:null,
__fY:null,
invalidateLayoutCache:function(){this.__fX=null;
},
renderLayout:function(e,
f){this.warn("Missing renderLayout() implementation!");
},
getSizeHint:function(){if(this.__fX){return this.__fX;
}return this.__fX=this._computeSizeHint();
},
_computeSizeHint:function(){return null;
},
invalidateChildrenCache:function(){this._invalidChildrenCache=true;
},
verifyLayoutProperty:null,
_clearSeparators:function(){var g=this.__fY;
if(g instanceof qx.ui.core.LayoutItem){g.clearSeparators();
}},
_renderSeparator:function(h,
i){this.__fY.renderSeparator(h,
i);
},
connectToWidget:function(g){if(g&&this.__fY){throw new Error("It is not possible to manually set the connected widget.");
}this.__fY=g;
this.invalidateChildrenCache();
},
_applyLayoutChange:function(){if(this.__fY){this.__fY.scheduleLayoutUpdate();
}},
_getLayoutChildren:function(){return this.__fY.getLayoutChildren();
}},
destruct:function(){this._disposeFields(d,
c);
}});
})();
(function(){var a="number",
b="string",
c="qx.ui.layout.Canvas";
qx.Class.define(c,
{extend:qx.ui.layout.Abstract,
members:{verifyLayoutProperty:null,
renderLayout:function(d,
e){var f=this._getLayoutChildren();
var g,
h,
j;
var k,
m,
n,
o,
p,
q;
var r,
s,
t,
u;
for(var v=0,
w=f.length;v<w;v++){g=f[v];
h=g.getSizeHint();
j=g.getLayoutProperties();
r=g.getMarginTop();
s=g.getMarginRight();
t=g.getMarginBottom();
u=g.getMarginLeft();
k=j.left!=null?j.left:j.edge;
if(k&&typeof k===b){k=Math.round(parseFloat(k)*d/100);
}n=j.right!=null?j.right:j.edge;
if(n&&typeof n===b){n=Math.round(parseFloat(n)*d/100);
}m=j.top!=null?j.top:j.edge;
if(m&&typeof m===b){m=Math.round(parseFloat(m)*e/100);
}o=j.bottom!=null?j.bottom:j.edge;
if(o&&typeof o===b){o=Math.round(parseFloat(o)*e/100);
}if(k!=null&&n!=null){p=d-k-n-u-s;
if(p<h.minWidth){p=h.minWidth;
}else if(p>h.maxWidth){p=h.maxWidth;
}k+=u;
}else{p=j.width;
if(p==null){p=h.width;
}else{p=Math.round(parseFloat(p)*d/100);
if(p<h.minWidth){p=h.minWidth;
}else if(p>h.maxWidth){p=h.maxWidth;
}}
if(n!=null){k=d-p-n-s-u;
}else if(k==null){k=u;
}else{k+=u;
}}if(m!=null&&o!=null){q=e-m-o-r-t;
if(q<h.minHeight){q=h.minHeight;
}else if(q>h.maxHeight){q=h.maxHeight;
}m+=r;
}else{q=j.height;
if(q==null){q=h.height;
}else{q=Math.round(parseFloat(q)*e/100);
if(q<h.minHeight){q=h.minHeight;
}else if(q>h.maxHeight){q=h.maxHeight;
}}
if(o!=null){m=e-q-o-t-r;
}else if(m==null){m=r;
}else{m+=r;
}}g.renderLayout(k,
m,
p,
q);
}},
_computeSizeHint:function(){var x=0,
y=0;
var z=0,
A=0;
var p,
B;
var q,
C;
var f=this._getLayoutChildren();
var g,
j,
D;
var k,
m,
n,
o;
for(var v=0,
w=f.length;v<w;v++){g=f[v];
j=g.getLayoutProperties();
D=g.getSizeHint();
var E=g.getMarginLeft()+g.getMarginRight();
var F=g.getMarginTop()+g.getMarginBottom();
p=D.width+E;
B=D.minWidth+E;
k=j.left!=null?j.left:j.edge;
if(k&&typeof k===a){p+=k;
B+=k;
}n=j.right!=null?j.right:j.edge;
if(n&&typeof n===a){p+=n;
B+=n;
}x=Math.max(x,
p);
y=Math.max(y,
B);
q=D.height+F;
C=D.minHeight+F;
m=j.top!=null?j.top:j.edge;
if(m&&typeof m===a){q+=m;
C+=m;
}o=j.bottom!=null?j.bottom:j.edge;
if(o&&typeof o===a){q+=o;
C+=o;
}z=Math.max(z,
q);
A=Math.max(A,
C);
}return {width:x,
minWidth:y,
height:z,
minHeight:A};
}}});
})();
(function(){var a="qx.html.Root";
qx.Class.define(a,
{extend:qx.html.Element,
construct:function(b){arguments.callee.base.call(this);
if(b!=null){this.useElement(b);
}},
members:{useElement:function(b){if(this._element){throw new Error("Elements could not be replaced!");
}b.$$hash=this.$$hash;
this._element=b;
this._root=true;
qx.html.Element._modified[this.$$hash]=this;
}}});
})();
(function(){var a="_applyLayoutChange",
b="top",
c="left",
d="middle",
e="Decorator",
f="__gd",
g="center",
h="baseline",
j="bottom",
k="qx.ui.layout.VBox",
m="__ga",
n="_applyReversed",
o="Integer",
p="__gb",
q="right",
r="Boolean";
qx.Class.define(k,
{extend:qx.ui.layout.Abstract,
construct:function(s,
t,
u){arguments.callee.base.call(this);
if(s){this.setSpacing(s);
}
if(t){this.setAlignY(t);
}
if(u){this.setSeparator(u);
}},
properties:{alignY:{check:[b,
d,
j],
init:b,
apply:a},
alignX:{check:[c,
g,
q,
h],
init:c,
apply:a},
spacing:{check:o,
init:0,
apply:a},
separator:{check:e,
nullable:true,
apply:a},
reversed:{check:r,
init:false,
apply:n}},
members:{__ga:null,
__gb:null,
__gc:null,
__gd:null,
_applyReversed:function(){this._invalidChildrenCache=true;
this._applyLayoutChange();
},
__ge:function(){var v=this._getLayoutChildren();
var w=v.length;
var x=false;
var y=this.__ga&&this.__ga.length!=w&&this.__gb&&this.__ga;
var z;
var A=y?this.__ga:new Array(w);
var B=y?this.__gb:new Array(w);
if(this.getReversed()){v=v.concat().reverse();
}for(var C=0;C<w;C++){z=v[C].getLayoutProperties();
if(z.height!=null){A[C]=parseFloat(z.height)/100;
}
if(z.flex!=null){B[C]=z.flex;
x=true;
}}if(!y){this.__ga=A;
this.__gb=B;
}this.__gc=x;
this.__gd=v;
delete this._invalidChildrenCache;
},
verifyLayoutProperty:null,
renderLayout:function(D,
E){if(this._invalidChildrenCache){this.__ge();
}var v=this.__gd;
var w=v.length;
var F=qx.ui.layout.Util;
var s=this.getSpacing();
var u=this.getSeparator();
if(u){var G=F.computeVerticalSeparatorGaps(v,
s,
u);
}else{var G=F.computeVerticalGaps(v,
s,
true);
}var C,
H,
I,
J;
var A=[];
var K=G;
for(C=0;C<w;C+=1){J=this.__ga[C];
I=J!=null?Math.floor((E-G)*J):v[C].getSizeHint().height;
A.push(I);
K+=I;
}if(this.__gc&&K!=E){var L={};
var M,
N;
for(C=0;C<w;C+=1){M=this.__gb[C];
if(M>0){Q=v[C].getSizeHint();
L[C]={min:Q.minHeight,
value:A[C],
max:Q.maxHeight,
flex:M};
}}var O=F.computeFlexOffsets(L,
E,
K);
for(C in O){N=O[C].offset;
A[C]+=N;
K+=N;
}}var P=v[0].getMarginTop();
if(K<E&&this.getAlignY()!=b){P=E-K;
if(this.getAlignY()===d){P=Math.round(P/2);
}}var Q,
R,
S,
I,
T,
U,
V;
var s=this.getSpacing();
this._clearSeparators();
if(u){var W=qx.theme.manager.Decoration.getInstance().resolve(u).getInsets();
var X=W.top+W.bottom;
}for(C=0;C<w;C+=1){H=v[C];
I=A[C];
Q=H.getSizeHint();
U=H.getMarginLeft();
V=H.getMarginRight();
S=Math.max(Q.minWidth,
Math.min(D-U-V,
Q.maxWidth));
R=F.computeHorizontalAlignOffset(H.getAlignX()||this.getAlignX(),
S,
D,
U,
V);
if(C>0){if(u){P+=T+s;
this._renderSeparator(u,
{top:P,
left:0,
height:X,
width:D});
P+=X+s+H.getMarginTop();
}else{P+=F.collapseMargins(s,
T,
H.getMarginTop());
}}H.renderLayout(R,
P,
S,
I);
P+=I;
T=H.getMarginBottom();
}},
_computeSizeHint:function(){if(this._invalidChildrenCache){this.__ge();
}var F=qx.ui.layout.Util;
var v=this.__gd;
var Y=0,
I=0;
var ba=0,
S=0;
var H,
Q,
bb;
for(var C=0,
bc=v.length;C<bc;C+=1){H=v[C];
Q=H.getSizeHint();
I+=Q.height;
Y+=this.__gb[C]>0?Q.minHeight:Q.height;
bb=H.getMarginLeft()+H.getMarginRight();
if((Q.width+bb)>S){S=Q.width+bb;
}if((Q.minWidth+bb)>ba){ba=Q.minWidth+bb;
}}var s=this.getSpacing();
var u=this.getSeparator();
if(u){var G=F.computeVerticalSeparatorGaps(v,
s,
u);
}else{var G=F.computeVerticalGaps(v,
s,
true);
}return {minHeight:Y+G,
height:I+G,
minWidth:ba,
width:S};
}},
destruct:function(){this._disposeFields(m,
p,
f);
}});
})();
(function(){var a="middle",
b="qx.ui.layout.Util",
c="left",
d="center",
e="top",
f="bottom",
g="right";
qx.Class.define(b,
{statics:{PERCENT_VALUE:/[0-9]+(?:\.[0-9]+)?%/,
computeFlexOffsets:function(h,
j,
k){var m,
n,
o,
p;
var q=j>k;
var r=Math.abs(j-k);
var s,
t;
var u={};
for(n in h){m=h[n];
u[n]={potential:q?m.max-m.value:m.value-m.min,
flex:q?m.flex:1/m.flex,
offset:0};
}while(r!=0){p=Infinity;
o=0;
for(n in u){m=u[n];
if(m.potential>0){o+=m.flex;
p=Math.min(p,
m.potential/m.flex);
}}if(o==0){break;
}p=Math.min(r,
p*o)/o;
s=0;
for(n in u){m=u[n];
if(m.potential>0){t=Math.min(r,
m.potential,
Math.ceil(p*m.flex));
s+=t-p*m.flex;
if(s>=1){s-=1;
t-=1;
}m.potential-=t;
if(q){m.offset+=t;
}else{m.offset-=t;
}r-=t;
}}}return u;
},
computeHorizontalAlignOffset:function(v,
w,
x,
y,
z){if(y==null){y=0;
}
if(z==null){z=0;
}var A=0;
switch(v){case c:A=y;
break;
case g:A=x-w-z;
break;
case d:A=Math.round((x-w)/2);
if(A<y){A=y;
}else if(A<z){A=Math.max(y,
x-w-z);
}break;
}return A;
},
computeVerticalAlignOffset:function(v,
B,
C,
D,
E){if(D==null){D=0;
}
if(E==null){E=0;
}var A=0;
switch(v){case e:A=D;
break;
case f:A=C-B-E;
break;
case a:A=Math.round((C-B)/2);
if(A<D){A=D;
}else if(A<E){A=Math.max(D,
C-B-E);
}break;
}return A;
},
collapseMargins:function(F){var G=0,
H=0;
for(var I=0,
J=arguments.length;I<J;I++){var A=arguments[I];
if(A<0){H=Math.min(H,
A);
}else if(A>0){G=Math.max(G,
A);
}}return G+H;
},
computeHorizontalGaps:function(K,
L,
M){if(L==null){L=0;
}var N=0;
if(M){N+=K[0].getMarginLeft();
for(var I=1,
J=K.length;I<J;I+=1){N+=this.collapseMargins(L,
K[I-1].getMarginRight(),
K[I].getMarginLeft());
}N+=K[J-1].getMarginRight();
}else{for(var I=1,
J=K.length;I<J;I+=1){N+=K[I].getMarginLeft()+K[I].getMarginRight();
}N+=(L*(J-1));
}return N;
},
computeVerticalGaps:function(K,
L,
M){if(L==null){L=0;
}var N=0;
if(M){N+=K[0].getMarginTop();
for(var I=1,
J=K.length;I<J;I+=1){N+=this.collapseMargins(L,
K[I-1].getMarginBottom(),
K[I].getMarginTop());
}N+=K[J-1].getMarginBottom();
}else{for(var I=1,
J=K.length;I<J;I+=1){N+=K[I].getMarginTop()+K[I].getMarginBottom();
}N+=(L*(J-1));
}return N;
},
computeHorizontalSeparatorGaps:function(K,
L,
O){var P=qx.theme.manager.Decoration.getInstance().resolve(O);
var Q=P.getInsets();
var w=Q.left+Q.right;
var N=0;
for(var I=0,
J=K.length;I<J;I++){var m=K[I];
N+=m.getMarginLeft()+m.getMarginRight();
}N+=(L+w+L)*(J-1);
return N;
},
computeVerticalSeparatorGaps:function(K,
L,
O){var P=qx.theme.manager.Decoration.getInstance().resolve(O);
var Q=P.getInsets();
var B=Q.top+Q.bottom;
var N=0;
for(var I=0,
J=K.length;I<J;I++){var m=K[I];
N+=m.getMarginTop()+m.getMarginBottom();
}N+=(L+B+L)*(J-1);
return N;
},
arrangeIdeals:function(R,
S,
T,
U,
V,
W){if(S<R||V<U){if(S<R&&V<U){S=R;
V=U;
}else if(S<R){V-=(R-S);
S=R;
if(V<U){V=U;
}}else if(V<U){S-=(U-V);
V=U;
if(S<R){S=R;
}}}
if(S>T||V>W){if(S>T&&V>W){S=T;
V=W;
}else if(S>T){V+=(S-T);
S=T;
if(V>W){V=W;
}}else if(V>W){S+=(V-W);
V=W;
if(S>T){S=T;
}}}return {begin:S,
end:V};
}}});
})();
(function(){var a="qx.ui.core.MLayoutHandling";
qx.Mixin.define(a,
{members:{setLayout:function(b){return this._setLayout(b);
},
getLayout:function(){return this._getLayout();
}},
statics:{remap:function(c){c.getLayout=c._getLayout;
c.setLayout=c._setLayout;
}}});
})();
(function(){var a="qx.event.type.Data",
b="qx.ui.container.Composite",
c="addChildWidget",
d="removeChildWidget";
qx.Class.define(b,
{extend:qx.ui.core.Widget,
include:[qx.ui.core.MChildrenHandling,
qx.ui.core.MLayoutHandling],
construct:function(e){arguments.callee.base.call(this);
if(e!=null){this._setLayout(e);
}},
events:{addChildWidget:a,
removeChildWidget:a},
members:{_afterAddChild:function(f){this.fireNonBubblingEvent(c,
qx.event.type.Data,
[f]);
},
_afterRemoveChild:function(f){this.fireNonBubblingEvent(d,
qx.event.type.Data,
[f]);
}},
defer:function(g,
h){qx.ui.core.MChildrenHandling.remap(h);
qx.ui.core.MLayoutHandling.remap(h);
}});
})();
(function(){var a="both",
b="qx.ui.menu.Menu",
c="_applySpacing",
d="icon",
e="label",
f="changeShow",
g="Integer",
h="qx.ui.toolbar.ToolBar",
j="toolbar",
k="changeOpenMenu";
qx.Class.define(h,
{extend:qx.ui.core.Widget,
include:qx.ui.core.MChildrenHandling,
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.HBox());
},
properties:{appearance:{refine:true,
init:j},
openMenu:{check:b,
event:k,
nullable:true},
show:{init:a,
check:[a,
e,
d],
inheritable:true,
event:f},
spacing:{nullable:true,
check:g,
themeable:true,
apply:c}},
members:{_applySpacing:function(m,
n){var o=this._getLayout();
m==null?o.resetSpacing():o.setSpacing(m);
},
addSpacer:function(){var p=new qx.ui.core.Spacer;
this._add(p,
{flex:1});
return p;
},
addSeparator:function(){this.add(new qx.ui.toolbar.Separator);
},
getMenuButtons:function(){var q=this.getChildren();
var r=[];
var s;
for(var t=0,
u=q.length;t<u;t++){s=q[t];
if(s instanceof qx.ui.toolbar.MenuButton){r.push(s);
}else if(s instanceof qx.ui.toolbar.Part){r.push.apply(r,
s.getMenuButtons());
}}return r;
}}});
})();
(function(){var a="qx.ui.menubar.MenuBar",
b="menubar";
qx.Class.define(a,
{extend:qx.ui.toolbar.ToolBar,
properties:{appearance:{refine:true,
init:b}}});
})();
(function(){var a="_applyLayoutChange",
b="left",
c="center",
d="top",
e="__gf",
f="Decorator",
g="middle",
h="__gg",
j="baseline",
k="bottom",
m="__gi",
n="Boolean",
o="right",
p="_applyReversed",
q="Integer",
r="qx.ui.layout.HBox";
qx.Class.define(r,
{extend:qx.ui.layout.Abstract,
construct:function(s,
t,
u){arguments.callee.base.call(this);
if(s){this.setSpacing(s);
}
if(t){this.setAlignX(t);
}
if(u){this.setSeparator(u);
}},
properties:{alignX:{check:[b,
c,
o],
init:b,
apply:a},
alignY:{check:[d,
g,
k,
j],
init:d,
apply:a},
spacing:{check:q,
init:0,
apply:a},
separator:{check:f,
nullable:true,
apply:a},
reversed:{check:n,
init:false,
apply:p}},
members:{__gf:null,
__gg:null,
__gh:null,
__gi:null,
_applyReversed:function(){this._invalidChildrenCache=true;
this._applyLayoutChange();
},
__gj:function(){var v=this._getLayoutChildren();
var w=v.length;
var x=false;
var y=this.__gf&&this.__gf.length!=w&&this.__gg&&this.__gf;
var z;
var A=y?this.__gf:new Array(w);
var B=y?this.__gg:new Array(w);
if(this.getReversed()){v=v.concat().reverse();
}for(var C=0;C<w;C++){z=v[C].getLayoutProperties();
if(z.width!=null){A[C]=parseFloat(z.width)/100;
}
if(z.flex!=null){B[C]=z.flex;
x=true;
}}if(!y){this.__gf=A;
this.__gg=B;
}this.__gh=x;
this.__gi=v;
delete this._invalidChildrenCache;
},
verifyLayoutProperty:null,
renderLayout:function(D,
E){if(this._invalidChildrenCache){this.__gj();
}var v=this.__gi;
var w=v.length;
var F=qx.ui.layout.Util;
var s=this.getSpacing();
var u=this.getSeparator();
if(u){var G=F.computeHorizontalSeparatorGaps(v,
s,
u);
}else{var G=F.computeHorizontalGaps(v,
s,
true);
}var C,
H,
I,
J;
var A=[];
var K=G;
for(C=0;C<w;C+=1){J=this.__gf[C];
I=J!=null?Math.floor((D-G)*J):v[C].getSizeHint().width;
A.push(I);
K+=I;
}if(this.__gh&&K!=D){var L={};
var M,
N;
for(C=0;C<w;C+=1){M=this.__gg[C];
if(M>0){Q=v[C].getSizeHint();
L[C]={min:Q.minWidth,
value:A[C],
max:Q.maxWidth,
flex:M};
}}var O=F.computeFlexOffsets(L,
D,
K);
for(C in O){N=O[C].offset;
A[C]+=N;
K+=N;
}}var P=v[0].getMarginLeft();
if(K<D&&this.getAlignX()!=b){P=D-K;
if(this.getAlignX()===c){P=Math.round(P/2);
}}var Q,
R,
S,
I,
T,
U,
V;
var s=this.getSpacing();
this._clearSeparators();
if(u){var W=qx.theme.manager.Decoration.getInstance().resolve(u).getInsets();
var X=W.left+W.right;
}for(C=0;C<w;C+=1){H=v[C];
I=A[C];
Q=H.getSizeHint();
U=H.getMarginTop();
V=H.getMarginBottom();
S=Math.max(Q.minHeight,
Math.min(E-U-V,
Q.maxHeight));
R=F.computeVerticalAlignOffset(H.getAlignY()||this.getAlignY(),
S,
E,
U,
V);
if(C>0){if(u){P+=T+s;
this._renderSeparator(u,
{left:P,
top:0,
width:X,
height:E});
P+=X+s+H.getMarginLeft();
}else{P+=F.collapseMargins(s,
T,
H.getMarginLeft());
}}H.renderLayout(P,
R,
I,
S);
P+=I;
T=H.getMarginRight();
}},
_computeSizeHint:function(){if(this._invalidChildrenCache){this.__gj();
}var F=qx.ui.layout.Util;
var v=this.__gi;
var Y=0,
I=0;
var ba=0,
S=0;
var H,
Q,
bb;
for(var C=0,
bc=v.length;C<bc;C+=1){H=v[C];
Q=H.getSizeHint();
I+=Q.width;
Y+=this.__gg[C]>0?Q.minWidth:Q.width;
bb=H.getMarginTop()+H.getMarginBottom();
if((Q.height+bb)>S){S=Q.height+bb;
}if((Q.minHeight+bb)>ba){ba=Q.minHeight+bb;
}}var s=this.getSpacing();
var u=this.getSeparator();
if(u){var G=F.computeHorizontalSeparatorGaps(v,
s,
u);
}else{var G=F.computeHorizontalGaps(v,
s,
true);
}return {minWidth:Y+G,
width:I+G,
minHeight:ba,
height:S};
}},
destruct:function(){this._disposeFields(e,
h,
m);
}});
})();
(function(){var a="qx.ui.core.Spacer";
qx.Class.define(a,
{extend:qx.ui.core.LayoutItem,
construct:function(b,
c){arguments.callee.base.call(this);
this.setWidth(b!=null?b:0);
this.setHeight(c!=null?c:0);
},
members:{destroy:function(){}}});
})();
(function(){var a="toolbar-separator",
b="qx.ui.toolbar.Separator";
qx.Class.define(b,
{extend:qx.ui.core.Widget,
properties:{appearance:{refine:true,
init:a},
width:{refine:true,
init:0},
height:{refine:true,
init:0}}});
})();
(function(){var a="label",
b="icon",
c="Boolean",
d="left",
e="both",
f="String",
g="_applyRich",
h="_applyIcon",
i="changeGap",
j="_applyShow",
k="right",
l="_applyCenter",
m="_applyIconPosition",
n="qx.ui.basic.Atom",
o="top",
p="changeShow",
q="bottom",
r="_applyLabel",
s="Integer",
t="_applyGap",
u="atom";
qx.Class.define(n,
{extend:qx.ui.core.Widget,
construct:function(v,
w){{};
arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.Atom());
if(v!=null){this.setLabel(v);
}
if(w!=null){this.setIcon(w);
}},
properties:{appearance:{refine:true,
init:u},
label:{apply:r,
nullable:true,
dispose:true,
check:f},
rich:{check:c,
init:false,
apply:g},
icon:{check:f,
apply:h,
nullable:true,
themeable:true},
gap:{check:s,
nullable:false,
event:i,
apply:t,
themeable:true,
init:4},
show:{init:e,
check:[e,
a,
b],
themeable:true,
inheritable:true,
apply:j,
event:p},
iconPosition:{init:d,
check:[o,
k,
q,
d],
themeable:true,
apply:m},
center:{init:false,
check:c,
themeable:true,
apply:l}},
members:{_createChildControlImpl:function(x){var y;
switch(x){case a:y=new qx.ui.basic.Label(this.getLabel());
y.setAnonymous(true);
y.setRich(this.getRich());
this._add(y);
if(this.getLabel()==null||this.getShow()===b){y.exclude();
}break;
case b:y=new qx.ui.basic.Image(this.getIcon());
y.setAnonymous(true);
this._addAt(y,
0);
if(this.getIcon()==null||this.getShow()===a){y.exclude();
}break;
}return y||arguments.callee.base.call(this,
x);
},
_forwardStates:{focused:true,
hovered:true},
_handleLabel:function(){if(this.getLabel()==null||this.getShow()===b){this._excludeChildControl(a);
}else{this._showChildControl(a);
}},
_handleIcon:function(){if(this.getIcon()==null||this.getShow()===a){this._excludeChildControl(b);
}else{this._showChildControl(b);
}},
_applyLabel:function(z,
A){var v=this._getChildControl(a,
true);
if(v){v.setContent(z);
}this._handleLabel();
},
_applyRich:function(z,
A){var v=this._getChildControl(a,
true);
if(v){v.setRich(z);
}},
_applyIcon:function(z,
A){var w=this._getChildControl(b,
true);
if(w){w.setSource(z);
}this._handleIcon();
},
_applyGap:function(z,
A){this._getLayout().setGap(z);
},
_applyShow:function(z,
A){this._handleLabel();
this._handleIcon();
},
_applyIconPosition:function(z,
A){this._getLayout().setIconPosition(z);
},
_applyCenter:function(z,
A){this._getLayout().setCenter(z);
}}});
})();
(function(){var a="changeEnabled",
b="qx.ui.core.MExecutable",
c="qx.event.Command",
d="qx.event.type.Event",
f="changeCommand",
g="_applyCommand",
h="execute";
qx.Mixin.define(b,
{events:{"execute":d},
properties:{command:{check:c,
apply:g,
event:f,
nullable:true}},
members:{execute:function(){var i=this.getCommand();
if(i){i.execute(this);
}this.fireEvent(h);
},
_applyCommand:function(j,
k){if(k){k.removeListener(a,
this._onChangeEnabledCommand,
this);
}
if(j){j.addListener(a,
this._onChangeEnabledCommand,
this);
if(this.getEnabled()===false){j.setEnabled(false);
}else if(j.getEnabled()===false){this.setEnabled(false);
}}},
_onChangeEnabledCommand:function(l){this.setEnabled(l.getData());
}}});
})();
(function(){var a="qx.event.type.Data",
b="string",
c="qx.ui.form.IFormElement",
d="boolean";
qx.Interface.define(c,
{events:{"changeValue":a,
"changeName":a,
"changeEnabled":a},
members:{setEnabled:function(e){this.assertType(e,
d);
},
getEnabled:function(){},
setName:function(e){this.assertType(e,
b);
},
getName:function(){},
setValue:function(e){this.assertType(e,
b);
},
getValue:function(){}}});
})();
(function(){var a="pressed",
b="abandoned",
c="hovered",
d="Enter",
f="Space",
g="String",
h="dblclick",
i="qx.ui.form.Button",
j="mouseup",
k="mousedown",
l="changeName",
m="mouseover",
n="mouseout",
o="changeValue",
p="keydown",
q="button",
r="keyup";
qx.Class.define(i,
{extend:qx.ui.basic.Atom,
include:qx.ui.core.MExecutable,
implement:qx.ui.form.IFormElement,
construct:function(s,
t,
u){arguments.callee.base.call(this,
s,
t);
if(u!=null){this.setCommand(u);
}this.addListener(m,
this._onMouseOver);
this.addListener(n,
this._onMouseOut);
this.addListener(k,
this._onMouseDown);
this.addListener(j,
this._onMouseUp);
this.addListener(p,
this._onKeyDown);
this.addListener(r,
this._onKeyUp);
this.addListener(h,
this._onStopEvent);
},
properties:{name:{check:g,
nullable:true,
event:l},
value:{check:g,
nullable:true,
event:o},
appearance:{refine:true,
init:q},
focusable:{refine:true,
init:true}},
members:{press:function(){if(this.hasState(b)){return;
}this.addState(a);
},
release:function(){if(this.hasState(a)){this.removeState(a);
}},
reset:function(){this.removeState(a);
this.removeState(b);
this.removeState(c);
},
_onMouseOver:function(v){if(!this.isEnabled()||v.getTarget()!==this){return;
}
if(this.hasState(b)){this.removeState(b);
this.addState(a);
}this.addState(c);
},
_onMouseOut:function(v){if(!this.isEnabled()||v.getTarget()!==this){return;
}this.removeState(c);
if(this.hasState(a)){this.removeState(a);
this.addState(b);
}},
_onMouseDown:function(v){if(!v.isLeftPressed()){return;
}v.stopPropagation();
this.capture();
this.removeState(b);
this.addState(a);
},
_onMouseUp:function(v){this.releaseCapture();
var w=this.hasState(a);
var x=this.hasState(b);
if(w){this.removeState(a);
}
if(x){this.removeState(b);
}else{this.addState(c);
if(w){this.execute();
}}v.stopPropagation();
},
_onKeyDown:function(v){switch(v.getKeyIdentifier()){case d:case f:this.removeState(b);
this.addState(a);
v.stopPropagation();
}},
_onKeyUp:function(v){switch(v.getKeyIdentifier()){case d:case f:if(this.hasState(a)){this.removeState(b);
this.removeState(a);
this.execute();
v.stopPropagation();
}}}}});
})();
(function(){var a="pressed",
b="hovered",
c="changeVisibility",
d="qx.ui.menu.Menu",
f="Enter",
g="changeMenu",
h="qx.ui.form.MenuButton",
i="abandoned",
j="_applyMenu";
qx.Class.define(h,
{extend:qx.ui.form.Button,
construct:function(k,
l,
m){arguments.callee.base.call(this,
k,
l);
if(m!=null){this.setMenu(m);
}},
properties:{menu:{check:d,
nullable:true,
apply:j,
event:g}},
members:{_applyMenu:function(n,
o){if(o){o.removeListener(c,
this._onMenuChange,
this);
o.resetOpener();
}
if(n){n.addListener(c,
this._onMenuChange,
this);
n.setOpener(this);
}},
open:function(p){var m=this.getMenu();
if(m){qx.ui.menu.Manager.getInstance().hideAll();
m.open();
if(p){var q=m.getChildren()[0];
if(q){m.setSelectedButton(q);
}}}},
_onMenuChange:function(r){var m=this.getMenu();
if(m.isVisible()){this.addState(a);
}else{this.removeState(a);
}},
_onMouseDown:function(r){var m=this.getMenu();
if(m){if(!m.isVisible()){this.open();
}else{m.exclude();
}r.stopPropagation();
}},
_onMouseUp:function(r){r.stopPropagation();
},
_onMouseOver:function(r){this.addState(b);
},
_onMouseOut:function(r){this.removeState(b);
},
_onKeyDown:function(r){switch(r.getKeyIdentifier()){case f:this.removeState(i);
this.addState(a);
var m=this.getMenu();
if(m){if(!m.isVisible()){this.open();
}else{m.exclude();
}}r.stopPropagation();
}},
_onKeyUp:function(r){}},
destruct:function(){if(this.getMenu()){if(!qx.core.ObjectRegistry.inShutDown){this.getMenu().destroy();
}}}});
})();
(function(){var a="pressed",
b="hovered",
c="inherit",
d="keydown",
f="qx.ui.toolbar.MenuButton",
g="keyup",
h="toolbar-button";
qx.Class.define(f,
{extend:qx.ui.form.MenuButton,
construct:function(i,
j,
k){arguments.callee.base.call(this,
i,
j,
k);
this.removeListener(d,
this._onKeyDown);
this.removeListener(g,
this._onKeyUp);
},
properties:{appearance:{refine:true,
init:h},
show:{refine:true,
init:c},
focusable:{refine:true,
init:false}},
members:{getToolBar:function(){var l=this;
while(l){if(l instanceof qx.ui.toolbar.ToolBar){return l;
}l=l.getLayoutParent();
}return null;
},
_onMenuChange:function(m){var k=this.getMenu();
var n=this.getToolBar();
if(k.isVisible()){this.addState(a);
if(n){n.setOpenMenu(k);
}}else{this.removeState(a);
if(n&&n.getOpenMenu()==k){n.resetOpenMenu();
}}},
_onMouseOver:function(m){this.addState(b);
if(this.getMenu()){var n=this.getToolBar();
var o=n.getOpenMenu();
if(o&&o!=this.getMenu()){qx.ui.menu.Manager.getInstance().hideAll();
this.open();
}}}}});
})();
(function(){var a="bottom",
b="_applyLayoutChange",
c="top",
d="left",
e="right",
f="middle",
g="center",
h="qx.ui.layout.Atom",
j="Integer",
k="Boolean";
qx.Class.define(h,
{extend:qx.ui.layout.Abstract,
properties:{gap:{check:j,
init:4,
apply:b},
iconPosition:{check:[d,
c,
e,
a],
init:d,
apply:b},
center:{check:k,
init:false,
apply:b}},
members:{verifyLayoutProperty:null,
renderLayout:function(l,
m){var n=qx.ui.layout.Util;
var o=this.getIconPosition();
var p=this._getLayoutChildren();
var q=p.length;
var r,
s,
t,
u;
var v,
w;
var x=this.getGap();
var y=this.getCenter();
if(o===a||o===e){var z=q-1;
var A=-1;
var B=-1;
}else{var z=0;
var A=q;
var B=1;
}if(o==c||o==a){if(y){var C=0;
for(var D=z;D!=A;D+=B){u=p[D].getSizeHint().height;
if(u>0){C+=u;
if(D!=z){C+=x;
}}}s=Math.round((m-C)/2);
}else{s=0;
}
for(var D=z;D!=A;D+=B){v=p[D];
w=v.getSizeHint();
t=Math.min(w.maxWidth,
Math.max(l,
w.minWidth));
u=w.height;
r=n.computeHorizontalAlignOffset(g,
t,
l);
v.renderLayout(r,
s,
t,
u);
if(u>0){s+=u+x;
}}}else{var E=l;
var F=0;
var G=null;
var H=0;
for(var D=z;D!=A;D+=B){v=p[D];
t=v.getSizeHint().width;
if(t>0){if(!G&&v instanceof qx.ui.basic.Label){G=v;
}else{E-=t;
}F+=t;
H++;
}}
if(H>1){var I=(H-1)*x;
E-=I;
F+=I;
}
if(y&&F<l){r=Math.round((l-F)/2);
}else{r=0;
}
for(var D=z;D!=A;D+=B){v=p[D];
w=v.getSizeHint();
u=Math.min(w.maxHeight,
Math.max(m,
w.minHeight));
if(v===G){t=Math.max(w.minWidth,
Math.min(E,
w.width));
}else{t=w.width;
}s=n.computeVerticalAlignOffset(f,
w.height,
m);
v.renderLayout(r,
s,
t,
u);
if(t>0){r+=t+x;
}}}},
_computeSizeHint:function(){var p=this._getLayoutChildren();
var q=p.length;
var w,
J;
if(q===1){var w=p[0].getSizeHint();
J={width:w.width,
height:w.height,
minWidth:w.minWidth,
minHeight:w.minHeight};
}else{var K=0,
t=0;
var L=0,
u=0;
var o=this.getIconPosition();
var x=this.getGap();
if(o===c||o===a){var H=0;
for(var D=0;D<q;D++){w=p[D].getSizeHint();
t=Math.max(t,
w.width);
K=Math.max(K,
w.minWidth);
if(w.height>0){u+=w.height;
L+=w.minHeight;
H++;
}}
if(H>1){var I=(H-1)*x;
u+=I;
L+=I;
}}else{var H=0;
for(var D=0;D<q;D++){w=p[D].getSizeHint();
u=Math.max(u,
w.height);
L=Math.max(L,
w.minHeight);
if(w.width>0){t+=w.width;
K+=w.minWidth;
H++;
}}
if(H>1){var I=(H-1)*x;
t+=I;
K+=I;
}}J={minWidth:K,
width:t,
minHeight:L,
height:u};
}return J;
}}});
})();
(function(){var a="qx.dynamicLocaleSwitch",
b="changeLocale",
c="on",
d="color",
f="qx.ui.basic.Label",
g="_applyRich",
h="_applyTextAlign",
i="Boolean",
j="_applyContent",
k="label",
l="__gk",
m="textAlign",
n="center",
o="A",
p="changeContent",
q="left",
r="String",
s="right";
qx.Class.define(f,
{extend:qx.ui.core.Widget,
construct:function(t){arguments.callee.base.call(this);
if(t!=null){this.setContent(t);
}
if(qx.core.Variant.isSet(a,
c)){qx.locale.Manager.getInstance().addListener(b,
this._onChangeLocale,
this);
}},
properties:{rich:{check:i,
init:false,
apply:g},
content:{check:r,
apply:j,
event:p,
nullable:true},
textAlign:{check:[q,
n,
s],
nullable:true,
themeable:true,
apply:h},
appearance:{refine:true,
init:k},
allowGrowX:{refine:true,
init:false},
allowGrowY:{refine:true,
init:false}},
members:{__gk:null,
__gl:null,
_getContentHint:function(){if(this.__gl){this.__gn();
delete this.__gl;
}return {width:this.__gm.width,
height:this.__gm.height};
},
_hasHeightForWidth:function(){return this.getRich();
},
_getContentHeightForWidth:function(u){if(!this.getRich()){return null;
}var v=this.__gk?this.__gk.getStyles():qx.bom.Font.getDefaultStyles();
return qx.bom.Label.getHtmlSize(this.getContent(),
v,
u).height;
},
_createContentElement:function(){return new qx.html.Label;
},
_applyTextAlign:function(w,
x){this.getContentElement().setStyle(m,
w);
},
_applyTextColor:function(w,
x){if(w){this.getContentElement().setStyle(d,
qx.theme.manager.Color.getInstance().resolve(w));
}else{this.getContentElement().removeStyle(d);
}},
__gm:{width:0,
height:0},
_applyFont:function(w,
x){var v;
if(w){this.__gk=qx.theme.manager.Font.getInstance().resolve(w);
v=this.__gk.getStyles();
}else{this.__gk=null;
v=qx.bom.Font.getDefaultStyles();
}this.getContentElement().setStyles(v);
this.__gl=true;
qx.ui.core.queue.Layout.add(this);
},
__gn:function(){var y=qx.bom.Label;
var z=this.getFont();
var v=z?this.__gk.getStyles():qx.bom.Font.getDefaultStyles();
var t=this.getContent()||o;
var A=this.getRich();
this.__gm=A?y.getHtmlSize(t,
v):y.getTextSize(t,
v);
},
_applyRich:function(w){this.getContentElement().setRich(w);
this.__gl=true;
qx.ui.core.queue.Layout.add(this);
},
_onChangeLocale:qx.core.Variant.select(a,
{"on":function(B){var t=this.getContent();
if(t.translate){this.setContent(t.translate());
}},
"off":null}),
_applyContent:function(w){this.getContentElement().setContent(w);
this.__gl=true;
qx.ui.core.queue.Layout.add(this);
}},
destruct:function(){if(qx.core.Variant.isSet(a,
c)){qx.locale.Manager.getInstance().removeListener(b,
this._onChangeLocale,
this);
}this._disposeFields(l);
}});
})();
(function(){var a="qx.bom.client.Locale",
b="-",
c="";
qx.Bootstrap.define(a,
{statics:{LOCALE:"",
VARIANT:"",
__go:function(){var d=(qx.bom.client.Engine.MSHTML?navigator.userLanguage:navigator.language).toLowerCase();
var e=c;
var f=d.indexOf(b);
if(f!=-1){e=d.substr(f+1);
d=d.substr(0,
f);
}this.LOCALE=d;
this.VARIANT=e;
}},
defer:function(g){g.__go();
}});
})();
(function(){var a="qx.core.BaseString";
qx.Class.define(a,
{extend:String,
construct:function(b){{};
this._txt=b;
},
members:{toString:function(){return this._txt;
},
valueOf:function(){return this._txt;
},
toHashCode:function(){return qx.core.ObjectRegistry.toHashCode(this);
},
base:function(c,
d){return qx.core.Object.prototype.base.apply(this,
arguments);
}},
defer:function(e){{};
}});
})();
(function(){var a="qx.locale.LocalizedString";
qx.Class.define(a,
{extend:qx.core.BaseString,
construct:function(b,
c,
d){arguments.callee.base.call(this,
b);
this.__gp=c;
this.__gq=d;
},
members:{translate:function(){return qx.locale.Manager.getInstance().translate(this.__gp,
this.__gq);
}}});
})();
(function(){var a="_",
b="",
c="qx.dynamicLocaleSwitch",
d="on",
e="__gs",
f="_applyLocale",
g="changeLocale",
h="__gr",
j="C",
k="qx.locale.Manager",
l="String",
m="singleton";
qx.Class.define(k,
{type:m,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this.__gr=window.qxtranslations||{};
this.__gs=window.qxlocales||{};
var n=qx.bom.client.Locale;
var o=n.LOCALE;
var p=n.VARIANT;
if(p!==b){o+=a+p;
}this.setLocale(o||this._defaultLocale);
},
statics:{tr:function(q,
r){var s=qx.lang.Array.fromArguments(arguments);
s.splice(0,
1);
return qx.locale.Manager.getInstance().translate(q,
s);
},
trn:function(t,
u,
v,
r){var s=qx.lang.Array.fromArguments(arguments);
s.splice(0,
3);
if(v!=1){return qx.locale.Manager.getInstance().translate(u,
s);
}else{return qx.locale.Manager.getInstance().translate(t,
s);
}},
trc:function(w,
q,
r){var s=qx.lang.Array.fromArguments(arguments);
s.splice(0,
2);
return qx.locale.Manager.getInstance().translate(q,
s);
},
marktr:function(q){return q;
}},
properties:{locale:{check:l,
nullable:true,
apply:f,
event:g}},
members:{_defaultLocale:j,
getLanguage:function(){return this._language;
},
getTerritory:function(){return this.getLocale().split(a)[1]||b;
},
getAvailableLocales:function(){var x=[];
for(var o in this.__gs){if(o!=this._defaultLocale){x.push(o);
}}return x;
},
__gt:function(o){var y;
var z=o.indexOf(a);
if(z==-1){y=o;
}else{y=o.substring(0,
z);
}return y;
},
_applyLocale:function(A,
B){this._locale=A;
this._language=this.__gt(A);
},
addTranslation:function(C,
D){var E=this.__gr;
if(E[C]){for(var F in D){E[C][F]=D[F];
}}else{E[C]=D;
}},
translate:function(q,
s,
o){var G;
var E=this.__gr;
if(!E){return q;
}
if(o){var y=this.__gt(o);
}else{o=this._locale;
y=this._language;
}
if(!G&&E[o]){G=E[o][q];
}
if(!G&&E[y]){G=E[y][q];
}
if(!G&&E[this._defaultLocale]){G=E[this._defaultLocale][q];
}
if(!G){G=q;
}
if(s.length>0){var H=[];
for(var I=0;I<s.length;I++){var J=s[I];
if(J.translate){H[I]=J.translate();
}else{H[I]=J;
}}G=qx.lang.String.format(G,
H);
}
if(qx.core.Variant.isSet(c,
d)){G=new qx.locale.LocalizedString(G,
q,
s);
}return G;
},
localize:function(q,
s,
o){var G;
var E=this.__gs;
if(!E){return q;
}
if(o){var y=this.__gt(o);
}else{o=this._locale;
y=this._language;
}
if(!G&&E[o]){G=E[o][q];
}
if(!G&&E[y]){G=E[y][q];
}
if(!G&&E[this._defaultLocale]){G=E[this._defaultLocale][q];
}
if(!G){G=q;
}
if(s.length>0){var H=[];
for(var I=0;I<s.length;I++){var J=s[I];
if(J.translate){H[I]=J.translate();
}else{H[I]=J;
}}G=qx.lang.String.format(G,
H);
}
if(qx.core.Variant.isSet(c,
d)){G=new qx.locale.LocalizedString(G,
q,
s);
}return G;
}},
destruct:function(){this._disposeFields(h,
e);
}});
})();
(function(){var a="qx.client",
b="gecko",
c="div",
d="",
e="hidden",
f="auto",
g="value",
h="text",
i="http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",
j="nowrap",
k="visible",
l="ellipsis",
m="normal",
n="label",
o="-1000px",
p="absolute",
q="px",
r="crop",
s="end",
t="100%",
u="qx.bom.Label",
v="opera",
w="block",
x="inherit",
y="none",
z="mshtml|opera";
qx.Class.define(u,
{statics:{__gu:{fontFamily:1,
fontSize:1,
fontWeight:1,
fontStyle:1,
lineHeight:1},
__gv:function(){var A=document.createElement(c);
var B=A.style;
B.width=B.height=f;
B.left=B.top=o;
B.visibility=e;
B.position=p;
B.overflow=k;
B.whiteSpace=j;
if(qx.core.Variant.isSet(a,
b)){var C=document.createElementNS(i,
n);
A.appendChild(C);
}document.body.insertBefore(A,
document.body.firstChild);
return this._textElement=A;
},
__gw:function(){var A=qx.bom.Element.create(c);
var B=A.style;
B.width=B.height=f;
B.left=B.top=o;
B.visibility=e;
B.position=p;
B.overflow=k;
B.whiteSpace=m;
document.body.insertBefore(A,
document.body.firstChild);
return this._htmlElement=A;
},
create:function(D,
E,
F){if(!F){F=window;
}
if(E){var A=F.document.createElement(c);
A.useHtml=true;
}else if(qx.core.Variant.isSet(a,
b)){var A=F.document.createElement(c);
var G=F.document.createElementNS(i,
n);
G.style.cursor=x;
G.style.overflow=e;
G.style.maxWidth=t;
G.setAttribute(r,
s);
A.appendChild(G);
}else{var A=F.document.createElement(c);
}
if(D){this.setContent(A,
D);
}return A;
},
getStyles:function(E){var H={};
if(E){H.whiteSpace=m;
}else if(qx.core.Variant.isSet(a,
b)){H.display=w;
}else{H.overflow=e;
H.whiteSpace=j;
H.textOverflow=l;
if(qx.core.Variant.isSet(a,
v)){H.OTextOverflow=l;
}}H.userSelect=y;
return H;
},
setContent:function(I,
J){J=J||d;
if(I.useHtml){I.innerHTML=J;
}else if(qx.core.Variant.isSet(a,
b)){I.firstChild.setAttribute(g,
J);
}else{qx.bom.element.Attribute.set(I,
h,
J);
}},
getContent:function(I){if(I.useHtml){return I.innerHTML;
}else if(qx.core.Variant.isSet(a,
b)){return I.firstChild.getAttribute(g)||d;
}else{return qx.bom.element.Attribute.get(I,
h);
}},
getHtmlSize:function(D,
H,
K){var I=this._htmlElement||this.__gw();
var L=this.__gu;
if(!H){H={};
}
for(var M in L){I.style[M]=H[M]||d;
}I.style.width=K!=null?K+q:f;
I.innerHTML=D;
return {width:I.clientWidth,
height:I.clientHeight};
},
getTextSize:function(N,
H){var I=this._textElement||this.__gv();
var L=this.__gu;
if(!H){H={};
}
for(var M in L){I.style[M]=H[M]||d;
}if(qx.core.Variant.isSet(a,
b)){I.firstChild.setAttribute(g,
N);
}else if(qx.core.Variant.isSet(a,
z)){I.innerText=N;
}else{I.textContent=N;
}var K=I.clientWidth;
var O=I.clientHeight;
if(qx.core.Variant.isSet(a,
b)){if(qx.bom.client.Platform.MAC){K++;
}}return {width:K,
height:O};
}}});
})();
(function(){var a="content",
b="qx.html.Label";
qx.Class.define(b,
{extend:qx.html.Element,
members:{_applyProperty:function(c,
d){arguments.callee.base.call(this,
c,
d);
if(c==a){qx.bom.Label.setContent(this._element,
d);
}},
_createDomElement:function(){var e=this.__gx;
var f=qx.bom.Label.create(this._content,
e);
var g=qx.bom.Label.getStyles(e);
for(var h in g){this.setStyle(h,
g[h]);
}return f;
},
setRich:function(d){if(this._element){throw new Error("The label mode cannot be modified after initial creation");
}d=!!d;
if(this.__gx==d){return;
}this.__gx=d;
return this;
},
setContent:function(d){this._setProperty(a,
d);
return this;
},
getContent:function(){return this._getProperty(a);
}}});
})();
(function(){var a="keypress",
b="mouseup",
c="mousedown",
d="interval",
f="keydown",
g="keyup",
h="Enter",
j="__gy",
k="Up",
l="Escape",
m="blur",
n="__gA",
o="qx.ui.menu.Manager",
p="Left",
q="Down",
r="Right",
s="__gz",
t="singleton",
u="Space";
qx.Class.define(o,
{type:t,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this.__gy=[];
var v=qx.core.Init.getApplication().getRoot();
v.addListener(c,
this._onMouseDown,
this,
true);
v.addListener(b,
this._onMouseUp,
this);
v.addListener(f,
this._onKeyUpDown,
this,
true);
v.addListener(g,
this._onKeyUpDown,
this,
true);
v.addListener(a,
this._onKeyPress,
this,
true);
qx.bom.Element.addListener(window,
m,
this.hideAll,
this);
this.__gz=new qx.event.Timer;
this.__gz.addListener(d,
this._onOpenInterval,
this);
this.__gA=new qx.event.Timer;
this.__gA.addListener(d,
this._onCloseInterval,
this);
},
members:{__gB:null,
__gC:null,
__gz:null,
__gA:null,
__gy:null,
_getChild:function(w,
x,
y,
z){var A=w.getChildren();
var B=A.length;
var C;
for(var D=x;D<B&&D>=0;D+=y){C=A[D];
if(C.isEnabled()&&!C.isAnonymous()){return C;
}}
if(z){D=D==B?0:B-1;
for(;D!=x;D+=y){C=A[D];
if(C.isEnabled()&&!C.isAnonymous()){return C;
}}}return null;
},
_isInMenu:function(E){while(E){if(E instanceof qx.ui.menu.Menu){return true;
}E=E.getLayoutParent();
}return false;
},
add:function(F){{};
var G=this.__gy;
G.push(F);
F.setZIndex(1e6+G.length);
},
remove:function(F){{};
var G=this.__gy;
if(G){qx.lang.Array.remove(G,
F);
}},
hideAll:function(){var G=this.__gy;
if(G){for(var D=G.length-1;D>=0;D--){G[D].exclude();
}}},
getActiveMenu:function(){var G=this.__gy;
return G.length>0?G[G.length-1]:null;
},
scheduleOpen:function(w){this.cancelClose(w);
if(w.isVisible()){if(this.__gB){this.cancelOpen(this.__gB);
}}else if(this.__gB!=w){this.__gB=w;
this.__gz.restartWith(w.getOpenInterval());
}},
scheduleClose:function(w){this.cancelOpen(w);
if(!w.isVisible()){if(this.__gC){this.cancelClose(this.__gC);
}}else if(this.__gC!=w){this.__gC=w;
this.__gA.restartWith(w.getCloseInterval());
}},
cancelOpen:function(w){if(this.__gB==w){this.__gz.stop();
this.__gB=null;
}},
cancelClose:function(w){if(this.__gC==w){this.__gA.stop();
this.__gC=null;
}},
_onOpenInterval:function(H){this.__gz.stop();
this.__gB.open();
this.__gB=null;
},
_onCloseInterval:function(H){this.__gA.stop();
this.__gC.exclude();
this.__gC=null;
},
_onMouseDown:function(H){var I=H.getTarget();
if(I.getMenu&&I.getMenu()&&I.getMenu().isVisible()){return;
}if(!this._isInMenu(I)){this.hideAll();
}},
_onMouseUp:function(H){var I=H.getTarget();
if(!(I instanceof qx.ui.menu.Menu)){this.hideAll();
}},
__gD:{"Enter":1,
"Space":1},
__gE:{"Escape":1,
"Up":1,
"Down":1,
"Left":1,
"Right":1},
_onKeyUpDown:function(H){var w=this.getActiveMenu();
if(!w){return;
}var J=H.getKeyIdentifier();
if(this.__gE[J]||(this.__gD[J]&&w.getSelectedButton())){H.stopPropagation();
}},
_onKeyPress:function(H){var w=this.getActiveMenu();
if(!w){return;
}var J=H.getKeyIdentifier();
var K=this.__gE[J];
var L=this.__gD[J];
if(K){switch(J){case k:this._onKeyPressUp(w);
break;
case q:this._onKeyPressDown(w);
break;
case p:this._onKeyPressLeft(w);
break;
case r:this._onKeyPressRight(w);
break;
case l:this.hideAll();
break;
}H.stopPropagation();
}else if(L){var M=w.getSelectedButton();
if(M){switch(J){case h:this._onKeyPressEnter(w,
M,
H);
break;
case u:this._onKeyPressSpace(w,
M,
H);
break;
}H.stopPropagation();
}}},
_onKeyPressUp:function(w){var N=w.getSelectedButton();
var A=w.getChildren();
var x=N?w.indexOf(N)-1:A.length-1;
var O=this._getChild(w,
x,
-1,
true);
if(O){w.setSelectedButton(O);
}else{w.resetSelectedButton();
}},
_onKeyPressDown:function(w){var N=w.getSelectedButton();
var x=N?w.indexOf(N)+1:0;
var O=this._getChild(w,
x,
1,
true);
if(O){w.setSelectedButton(O);
}else{w.resetSelectedButton();
}},
_onKeyPressLeft:function(w){var P=w.getOpener();
if(!P){return;
}if(P instanceof qx.ui.menu.Button){var Q=P.getLayoutParent();
Q.resetOpenedButton();
Q.setSelectedButton(P);
}else if(P instanceof qx.ui.toolbar.MenuButton){var R=P.getToolBar().getMenuButtons();
var S=R.indexOf(P);
if(S===-1){return;
}var T=S==0?R[R.length-1]:R[S-1];
if(T!=P){T.open(true);
}}},
_onKeyPressRight:function(w){var N=w.getSelectedButton();
if(N){var U=N.getMenu();
if(U){w.setOpenedButton(N);
var V=this._getChild(U,
0,
1);
if(V){U.setSelectedButton(V);
}return;
}}else if(!w.getOpenedButton()){var V=this._getChild(w,
0,
1);
if(V){w.setSelectedButton(V);
if(V.getMenu()){w.setOpenedButton(V);
}return;
}}var P=w.getOpener();
if(P instanceof qx.ui.menu.Button&&N){while(P){P=P.getLayoutParent();
if(P instanceof qx.ui.menu.Menu){P=P.getOpener();
if(P instanceof qx.ui.toolbar.MenuButton){break;
}}else{break;
}}
if(!P){return;
}}if(P instanceof qx.ui.toolbar.MenuButton){var R=P.getToolBar().getMenuButtons();
var S=R.indexOf(P);
if(S===-1){return;
}var W=R[S+1];
if(!W){W=R[0];
}
if(W!=P){W.open(true);
}}},
_onKeyPressEnter:function(w,
M,
H){if(M.hasListener(a)){var X=H.clone();
X.setBubbles(false);
X.setTarget(M);
M.dispatchEvent(X);
}this.hideAll();
},
_onKeyPressSpace:function(w,
M,
H){if(M.hasListener(a)){var X=H.clone();
X.setBubbles(false);
X.setTarget(M);
M.dispatchEvent(X);
}}},
destruct:function(){var v=qx.core.Init.getApplication().getRoot();
if(v){v.removeListener(c,
this._onMouseDown,
this,
true);
v.removeListener(b,
this._onMouseUp,
this);
v.removeListener(f,
this._onKeyUpDown,
this,
true);
v.removeListener(g,
this._onKeyUpDown,
this,
true);
v.removeListener(a,
this._onKeyPress,
this,
true);
}this._disposeObjects(s,
n);
this._disposeArray(j);
}});
})();
(function(){var a="interval",
b="qx.event.Timer",
c="__gF",
d="_applyInterval",
f="_applyEnabled",
g="Boolean",
h="__gG",
i="qx.event.type.Event",
j="Integer";
qx.Class.define(b,
{extend:qx.core.Object,
construct:function(k){arguments.callee.base.call(this);
this.setEnabled(false);
if(k!=null){this.setInterval(k);
}this.__gF=qx.lang.Function.bind(this._oninterval,
this);
},
events:{"interval":i},
statics:{once:function(l,
m,
n){var o=new qx.event.Timer(n);
o.addListener(a,
function(p){l.call(m,
p);
o.dispose();
m=null;
},
m);
o.start();
return o;
}},
properties:{enabled:{init:true,
check:g,
apply:f},
interval:{check:j,
init:1000,
apply:d}},
members:{__gG:null,
_applyInterval:function(q,
r){if(this.getEnabled()){this.restart();
}},
_applyEnabled:function(q,
r){if(r){window.clearInterval(this.__gG);
this.__gG=null;
}else if(q){this.__gG=window.setInterval(this.__gF,
this.getInterval());
}},
start:function(){this.setEnabled(true);
},
startWith:function(k){this.setInterval(k);
this.start();
},
stop:function(){this.setEnabled(false);
},
restart:function(){this.stop();
this.start();
},
restartWith:function(k){this.stop();
this.startWith(k);
},
_oninterval:function(){if(this.getEnabled()){this.fireEvent(a);
}}},
destruct:function(){if(this.__gG){window.clearInterval(this.__gG);
}this._disposeFields(h,
c);
}});
})();
(function(){var a="Integer",
b="qx.ui.core.Widget",
c="visible",
d="selected",
f="qx.ui.menu.Menu",
g="_applyOpenInterval",
h="_applyOpenedButton",
i="_applyArrowColumnWidth",
j="_applyIconColumnWidth",
k="mouseover",
l="mouseout",
m="excluded",
n="_applySpacingX",
o="_applyCloseInterval",
p="_applySelectedButton",
q="menu",
r="_applySpacingY";
qx.Class.define(f,
{extend:qx.ui.core.Widget,
include:[qx.ui.core.MPlacement,
qx.ui.core.MChildrenHandling],
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.menu.Layout);
this.getApplicationRoot().add(this);
this.addListener(k,
this._onMouseOver);
this.addListener(l,
this._onMouseOut);
this.initVisibility();
},
properties:{appearance:{refine:true,
init:q},
allowGrowX:{refine:true,
init:false},
allowGrowY:{refine:true,
init:false},
visibility:{refine:true,
init:m},
keepFocus:{refine:true,
init:true},
keepActive:{refine:true,
init:true},
spacingX:{check:a,
apply:n,
init:0,
themeable:true},
spacingY:{check:a,
apply:r,
init:0,
themeable:true},
iconColumnWidth:{check:a,
init:0,
themeable:true,
apply:j},
arrowColumnWidth:{check:a,
init:0,
themeable:true,
apply:i},
selectedButton:{check:b,
nullable:true,
apply:p},
openedButton:{check:b,
nullable:true,
apply:h},
opener:{check:b,
nullable:true},
openInterval:{check:a,
themeable:true,
init:250,
apply:g},
closeInterval:{check:a,
themeable:true,
init:250,
apply:o}},
members:{__gH:null,
open:function(){this.placeToWidget(this.getOpener());
this.show();
},
addSeparator:function(){this.add(new qx.ui.menu.Separator);
},
getColumnSizes:function(){return this._getLayout().getColumnSizes();
},
_applyIconColumnWidth:function(s,
t){this._getLayout().setIconColumnWidth(s);
},
_applyArrowColumnWidth:function(s,
t){this._getLayout().setArrowColumnWidth(s);
},
_applySpacingX:function(s,
t){this._getLayout().setColumnSpacing(s);
},
_applySpacingY:function(s,
t){this._getLayout().setSpacing(s);
},
_applyVisibility:function(s,
t){arguments.callee.base.call(this,
s,
t);
var u=qx.ui.menu.Manager.getInstance();
if(s===c){u.add(this);
var v=this.getOpener();
var w=v.getLayoutParent();
if(w&&w instanceof qx.ui.menu.Menu){w.setOpenedButton(v);
}}else if(t===c){u.remove(this);
var v=this.getOpener();
var w=v.getLayoutParent();
if(w&&w instanceof qx.ui.menu.Menu&&w.getOpenedButton()==v){w.resetOpenedButton();
}this.resetOpenedButton();
this.resetSelectedButton();
}},
_applySelectedButton:function(s,
t){if(t){t.removeState(d);
}
if(s){s.addState(d);
}},
_applyOpenedButton:function(s,
t){if(t){t.getMenu().exclude();
}
if(s){s.getMenu().open();
}},
_onMouseOver:function(x){var u=qx.ui.menu.Manager.getInstance();
u.cancelClose(this);
var y=x.getTarget();
if(y.isEnabled()&&y instanceof qx.ui.menu.AbstractButton){this.setSelectedButton(y);
var z=y.getMenu&&y.getMenu();
if(z){u.scheduleOpen(z);
this.__gH=z;
}else{var A=this.getOpenedButton();
if(A){u.scheduleClose(A.getMenu());
}
if(this.__gH){u.cancelOpen(this.__gH);
this.__gH=null;
}}}else if(!this.getOpenedButton()){this.resetSelectedButton();
}},
_onMouseOut:function(x){var u=qx.ui.menu.Manager.getInstance();
if(!qx.ui.core.Widget.contains(this,
x.getRelatedTarget())){var A=this.getOpenedButton();
A?this.setSelectedButton(A):this.resetSelectedButton();
if(A){u.cancelClose(A.getMenu());
}if(this.__gH){u.cancelOpen(this.__gH);
}}}}});
})();
(function(){var a="Integer",
b="_applyLayoutChange",
c="qx.ui.menu.Layout";
qx.Class.define(c,
{extend:qx.ui.layout.VBox,
properties:{columnSpacing:{check:a,
init:0,
apply:b},
spanColumn:{check:a,
init:1,
nullable:true,
apply:b},
iconColumnWidth:{check:a,
init:0,
themeable:true,
apply:b},
arrowColumnWidth:{check:a,
init:0,
themeable:true,
apply:b}},
members:{__gI:null,
_computeSizeHint:function(){var d=this._getLayoutChildren();
var e,
f,
g;
var h=this.getSpanColumn();
var j=this.__gI=[0,
0,
0,
0];
var k=this.getColumnSpacing();
var m=0;
var n=0;
for(var o=0,
p=d.length;o<p;o++){e=d[o];
if(e.isAnonymous()){continue;
}f=e.getChildrenSizes();
for(var q=0;q<f.length;q++){if(h!=null&&q==h&&f[h+1]==0){m=Math.max(m,
f[q]);
}else{j[q]=Math.max(j[q],
f[q]);
}}var r=d[o].getInsets();
n=Math.max(n,
r.left+r.right);
}if(h!=null&&j[h]+k+j[h+1]<m){j[h]=m-j[h+1]-k;
}if(m==0){g=k*2;
}else{g=k*3;
}if(j[0]==0){j[0]=this.getIconColumnWidth();
}if(j[3]==0){j[3]=this.getArrowColumnWidth();
}return {height:arguments.callee.base.call(this).height,
width:qx.lang.Array.sum(j)+n+g};
},
getColumnSizes:function(){return this.__gI||null;
}}});
})();
(function(){var a="menu-separator",
b="qx.ui.menu.Separator";
qx.Class.define(b,
{extend:qx.ui.core.Widget,
properties:{appearance:{refine:true,
init:a},
anonymous:{refine:true,
init:true}}});
})();
(function(){var a="icon",
b="label",
c="arrow",
d="shortcut",
f="submenu",
g="String",
h="qx.ui.menu.Menu",
i="qx.ui.menu.AbstractButton",
j="keypress",
k="_applyIcon",
l="mouseup",
m="abstract",
n="_applyLabel",
o="_applyMenu";
qx.Class.define(i,
{extend:qx.ui.core.Widget,
type:m,
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.menu.ButtonLayout);
this.addListener(l,
this._onMouseUp);
this.addListener(j,
this._onKeyPress);
},
properties:{label:{check:g,
apply:n,
nullable:true},
menu:{check:h,
apply:o,
nullable:true},
icon:{check:g,
apply:k,
themeable:true,
nullable:true}},
members:{_createChildControlImpl:function(p){var q;
switch(p){case a:q=new qx.ui.basic.Image;
q.setAnonymous(true);
this._add(q,
{column:0});
break;
case b:q=new qx.ui.basic.Label;
q.setAnonymous(true);
this._add(q,
{column:1});
break;
case d:q=new qx.ui.basic.Label;
q.setAnonymous(true);
this._add(q,
{column:2});
break;
case c:q=new qx.ui.basic.Image;
q.setAnonymous(true);
this._add(q,
{column:3});
break;
}return q||arguments.callee.base.call(this,
p);
},
_forwardStates:{selected:1},
getChildrenSizes:function(){var r=0,
s=0,
t=0,
u=0;
if(this._isChildControlVisible(a)){var v=this._getChildControl(a);
r=v.getMarginLeft()+v.getSizeHint().width+v.getMarginRight();
}
if(this._isChildControlVisible(b)){var w=this._getChildControl(b);
s=w.getMarginLeft()+w.getSizeHint().width+w.getMarginRight();
}
if(this._isChildControlVisible(d)){var x=this._getChildControl(d);
t=x.getMarginLeft()+x.getSizeHint().width+x.getMarginRight();
}
if(this._isChildControlVisible(c)){var y=this._getChildControl(c);
u=y.getMarginLeft()+y.getSizeHint().width+y.getMarginRight();
}return [r,
s,
t,
u];
},
_onMouseUp:function(z){},
_onKeyPress:function(z){},
_applyIcon:function(A,
B){if(A){this._showChildControl(a).setSource(A);
}else{this._excludeChildControl(a);
}},
_applyLabel:function(A,
B){if(A){this._showChildControl(b).setContent(A);
}else{this._excludeChildControl(b);
}},
_applyMenu:function(A,
B){if(B){B.resetOpener();
B.removeState(f);
}
if(A){this._showChildControl(c);
A.setOpener(this);
A.addState(f);
}else{this._excludeChildControl(c);
}}},
destruct:function(){if(this.getMenu()){if(!qx.core.ObjectRegistry.inShutDown){this.getMenu().destroy();
}}}});
})();
(function(){var a="middle",
b="qx.ui.menu.ButtonLayout",
c="left";
qx.Class.define(b,
{extend:qx.ui.layout.Abstract,
members:{verifyLayoutProperty:null,
renderLayout:function(d,
e){var f=this._getLayoutChildren();
var g;
var h;
var j=[];
for(var k=0,
m=f.length;k<m;k++){g=f[k];
h=g.getLayoutProperties().column;
j[h]=g;
}var n=f[0].getLayoutParent().getLayoutParent();
var o=n.getColumnSizes();
var p=n.getSpacingX();
var q=0,
r=0;
var s=qx.ui.layout.Util;
for(var k=0,
m=o.length;k<m;k++){g=j[k];
if(g){var t=g.getSizeHint();
var r=s.computeVerticalAlignOffset(g.getAlignY()||a,
t.height,
e,
0,
0);
var u=s.computeHorizontalAlignOffset(g.getAlignX()||c,
t.width,
o[k],
g.getMarginLeft(),
g.getMarginRight());
g.renderLayout(q+u,
r,
t.width,
t.height);
}q+=o[k]+p;
}},
_computeSizeHint:function(){var f=this._getLayoutChildren();
var v=0;
for(var k=0,
m=f.length;k<m;k++){v=Math.max(v,
f[k].getSizeHint().height);
}return {width:0,
height:v};
}}});
})();
(function(){var a="shortcut",
b="qx.ui.menu.Button",
c="changeCommand",
d="menu-button";
qx.Class.define(b,
{extend:qx.ui.menu.AbstractButton,
include:qx.ui.core.MExecutable,
construct:function(f,
g,
h,
i){arguments.callee.base.call(this);
this.addListener(c,
this._onChangeCommand,
this);
if(f!=null){this.setLabel(f);
}
if(g!=null){this.setIcon(g);
}
if(h!=null){this.setCommand(h);
}
if(i!=null){this.setMenu(i);
}},
properties:{appearance:{refine:true,
init:d}},
members:{_onChangeCommand:function(j){this._getChildControl(a).setContent(j.getData().toString());
},
_onMouseUp:function(j){if(j.isLeftPressed()){this.execute();
if(this.getMenu()){j.stopPropagation();
}}},
_onKeyPress:function(j){this.execute();
}}});
})();
(function(){var a="qx.ui.core.MRemoteChildrenHandling";
qx.Mixin.define(a,
{members:{getChildren:function(){return this.getChildrenContainer().getChildren();
},
hasChildren:function(){return this.getChildrenContainer().hasChildren();
},
add:function(b,
c){return this.getChildrenContainer().add(b,
c);
},
remove:function(b){return this.getChildrenContainer().remove(b);
},
removeAll:function(){return this.getChildrenContainer().removeAll();
},
indexOf:function(b){return this.getChildrenContainer().indexOf(b);
},
addAt:function(b,
d,
c){return this.getChildrenContainer().addAt(b,
d,
c);
},
addBefore:function(b,
e,
c){return this.getChildrenContainer().addBefore(b,
e,
c);
},
addAfter:function(b,
f,
c){return this.getChildrenContainer().addAfter(b,
f,
c);
},
removeAt:function(d){return this.getChildrenContainer().removeAt(d);
}}});
})();
(function(){var a="container",
b="handle",
c="both",
d="Integer",
e="middle",
f="qx.ui.toolbar.Part",
g="icon",
h="label",
j="changeShow",
k="_applySpacing",
m="toolbar/part";
qx.Class.define(f,
{extend:qx.ui.core.Widget,
include:[qx.ui.core.MRemoteChildrenHandling],
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.HBox);
this._createChildControl(b);
},
properties:{appearance:{refine:true,
init:m},
show:{init:c,
check:[c,
h,
g],
inheritable:true,
event:j},
spacing:{nullable:true,
check:d,
themeable:true,
apply:k}},
members:{_createChildControlImpl:function(n){var o;
switch(n){case b:o=new qx.ui.basic.Image();
o.setAlignY(e);
this._add(o);
break;
case a:o=new qx.ui.toolbar.PartContainer;
this._add(o);
break;
}return o||arguments.callee.base.call(this,
n);
},
getChildrenContainer:function(){return this._getChildControl(a);
},
_applySpacing:function(p,
q){var r=this._getChildControl(a).getLayout();
p==null?r.resetSpacing():r.setSpacing(p);
},
addSeparator:function(){this.add(new qx.ui.toolbar.Separator);
},
getMenuButtons:function(){var s=this.getChildren();
var t=[];
var u;
for(var v=0,
w=s.length;v<w;v++){u=s[v];
if(u instanceof qx.ui.toolbar.MenuButton){t.push(u);
}}return t;
}}});
})();
(function(){var a="both",
b="toolbar/part/container",
c="icon",
d="changeShow",
e="qx.ui.toolbar.PartContainer",
f="label";
qx.Class.define(e,
{extend:qx.ui.container.Composite,
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.HBox);
},
properties:{appearance:{refine:true,
init:b},
show:{init:a,
check:[a,
f,
c],
inheritable:true,
event:d}}});
})();
(function(){var a="list",
b="test",
c="Test",
d="Help",
f="File",
g="",
h="execute",
j="resultViewXml.xslt",
k="resultView.pdf",
l="changeMenu",
m="../",
n="org.argeo.slc.web.event.CommandsManager",
o="resultView.xls",
p="About...",
q="Control+o",
r="Close",
s="both",
t='100%',
u="pdf",
v="resource/slc/help-contents.png",
w="resource/slc/process-stop.png",
x="@commandid",
y="Show Text",
z="resultView.xslt",
A='report[@type="download"]',
B="Copy to...",
C="Download as...",
D="Load Tests",
E="Control+l",
F="Control+c",
G="SLC is a product from Argeo.",
H="xls",
I="Show Icons",
J='south',
K="About SLC",
L="Delete",
M="Control+q",
N="resource/slc/go-down.png",
O="Control+s",
P="Control+h",
Q="Open",
R=".png",
S="resource/slc/view-refresh.png",
T="icon",
U="Stop",
V="resource/slc/system-shutdown.png",
W="/org.argeo.slc.webapp/resultList.web",
X="changeValue",
Y="resource/slc/edit-copy.png",
ba="Quit",
bb='report[@type="applet"]',
bc='param[@name="uuid"]',
bd="Toggle Console",
be="label",
bf="resource/slc/mime-",
bg="resource/slc/document-open.png",
bh="resource/slc/help-about.png",
bi='center',
bj="Control+d",
bk="Show Both",
bl="?uuid=",
bm="resource/slc/edit-delete.png";
qx.Class.define(n,
{extend:qx.core.Object,
construct:function(bn){arguments.callee.base.call(this);
this.application=bn;
},
properties:{definitions:{init:{"loadtestlist":{label:D,
icon:S,
shortcut:E,
enabled:true,
menu:f,
toolbar:a,
callback:function(bo){this.loadTable(W);
},
command:null},
"stop":{label:U,
icon:w,
shortcut:O,
enabled:false,
menu:null,
toolbar:a,
callback:function(bo){},
command:null},
"quit":{label:ba,
icon:V,
shortcut:M,
enabled:true,
menu:f,
toolbar:false,
callback:function(bo){},
command:null},
"opentest":{label:Q,
icon:bg,
shortcut:q,
enabled:false,
menu:c,
toolbar:b,
callback:function(bo){var bp=this.getSelectionForView(a).getNodes();
this.createTestApplet(bp[0]);
},
selectionChange:function(bq,
bp){if(bq!=a)return;
this.setEnabled(false);
if(bp==null||!bp.length)return;
var br=org.argeo.slc.web.util.Element.selectSingleNode(bp[0],
bb);
if(br!=null&&qx.dom.Node.getText(br)!=g){this.setEnabled(true);
}},
command:null},
"download":{label:C,
icon:N,
shortcut:null,
enabled:false,
menu:c,
toolbar:b,
callback:function(bo){},
command:null,
submenu:{},
submenuCallback:function(bs){var bp=this.getSelectionForView(a).getNodes();
var bt=qx.xml.Element.getSingleNodeText(bp[0],
bc);
var bu={xsl:z,
xml:j,
xls:o,
pdf:k};
var bv=m+bu[bs]+bl+bt;
if(bs==H||bs==u){document.location.href=bv;
}else{var bw=window.open(bv);
}},
selectionChange:function(bq,
bp){if(bq!=a)return;
this.clearMenus();
this.setEnabled(false);
if(bp==null)return;
var bx=qx.xml.Element.selectNodes(bp[0],
A);
if(bx==null||!bx.length)return;
for(var by=0;by<bx.length;by++){var bz=bx[by];
var bs=qx.dom.Node.getText(org.argeo.slc.web.util.Element.selectSingleNode(bz,
x));
this.addSubMenuButton(qx.dom.Node.getText(bz),
bf+bs+R,
bs);
}this.setEnabled(true);
this.fireDataEvent(l,
this.getMenu());
}},
"deletetest":{label:L,
icon:bm,
shortcut:bj,
enabled:false,
menu:c,
toolbar:b,
callback:function(bo){},
command:null},
"copytocollection":{label:B,
icon:Y,
shortcut:F,
enabled:false,
menu:c,
toolbar:b,
callback:function(bo){},
command:null},
"log":{label:bd,
icon:v,
shortcut:g,
enabled:true,
menu:d,
toolbar:false,
callback:function(bo){qx.log.appender.Console.toggle();
},
command:null},
"help":{label:p,
icon:bh,
shortcut:P,
enabled:true,
menu:d,
toolbar:false,
callback:function(bo){var bA=new qx.ui.window.Window(K);
bA.set({showMaximize:false,
showMinimize:false,
width:200,
height:150});
bA.setLayout(new qx.ui.layout.Dock());
bA.add(new qx.ui.basic.Label(G),
{edge:bi,
width:t});
var bB=new qx.ui.form.Button(r);
bB.addListener(h,
function(bo){this.hide();
this.destroy();
},
bA);
bA.add(bB,
{edge:J});
bA.setModal(true);
bA.center();
this.getRoot().add(bA);
bA.show();
},
command:null}}}},
members:{createCommands:function(){this.menus={};
this.toolbars={};
var bC=this.getDefinitions();
for(var bD in bC){var bE=bC[bD];
var bF=new org.argeo.slc.web.event.Command(bD,
bE.label,
bE.icon,
bE.shortcut);
if(bE.submenu){var bG=new qx.ui.menu.Menu();
bF.setMenu(bG);
if(bE.submenuCallback){bF.setMenuCallback(bE.submenuCallback);
bF.setMenuContext(this.application);
}}bF.setEnabled(bE.enabled);
bF.addListener(h,
bE.callback,
this.application);
bE.command=bF;
if(bE.menu){if(!this.menus[bE.menu])this.menus[bE.menu]=[];
this.menus[bE.menu].push(bF);
}
if(bE.toolbar){if(!this.toolbars[bE.toolbar])this.toolbars[bE.toolbar]=[];
this.toolbars[bE.toolbar].push(bF);
}}this.setDefinitions(bC);
},
refreshCommands:function(bH){var bC=this.getDefinitions();
var bp=null;
if(bH.getCount()>0){var bp=bH.getNodes();
}
for(var bD in bC){var bE=bC[bD];
if(!bE.selectionChange)continue;
var bI=qx.lang.Function.bind(bE.selectionChange,
bE.command);
bI(bH.getViewId(),
bp);
}},
createMenuButtons:function(bJ){for(var bD in this.menus){var bG=new qx.ui.menu.Menu();
var bK=new qx.ui.menubar.Button(bD,
null,
bG);
for(var by=0;by<this.menus[bD].length;by++){bG.add(this.menus[bD][by].getMenuButton());
}bJ.add(bK);
}},
createToolbarParts:function(bL){for(var bD in this.toolbars){var bM=new qx.ui.toolbar.Part();
bL.add(bM);
this.toolbars[bD].map(function(bF){bM.add(bF.getToolbarButton());
});
}},
createMenuFromIds:function(bN){var bC=this.getDefinitions();
var bO=new qx.ui.menu.Menu();
for(var by=0;by<bN.length;by++){var bE=bC[bN[by]];
var bF=bE.command;
bO.add(bF.getMenuButton());
}return bO;
},
executeCommand:function(bs){var bC=this.getDefinitions();
if(bC[bs]&&bC[bs].command.getEnabled()){bC[bs].command.execute();
}},
getCommandById:function(bs){var bC=this.getDefinitions();
if(bC[bs]&&bC[bs].command){return bC[bs].command;
}},
addToolbarContextMenu:function(bL){var bG=new qx.ui.menu.Menu();
var bP=new qx.ui.menu.RadioButton(I);
bP.setValue(T);
var bQ=new qx.ui.menu.RadioButton(y);
bQ.setValue(be);
var bR=new qx.ui.menu.RadioButton(bk);
bR.setValue(s);
var bS=new qx.ui.form.RadioGroup(bP,
bQ,
bR);
bG.add(bP);
bG.add(bQ);
bG.add(bR);
bL.setContextMenu(bG);
bS.addListener(X,
function(bo){this.setShow(bo.getData());
},
bL);
}}});
})();
(function(){var a="qx.client",
b='"',
c='="',
d='xmlns:',
e=" ",
f='SelectionNamespaces',
g="'",
h="xmlns:ns='",
j="org.argeo.slc.web.util.Element",
k="SelectionLanguage",
l="SelectionNamespaces",
m="XPath",
n='descendant-or-self::ns:';
qx.Class.define(j,
{statics:{selectSingleNode:qx.core.Variant.select(a,
{"mshtml|opera":function(o,
p,
q){if(q){var r=[];
var s=0;
for(var t in q){r[s]=d+t+c+q[t]+b;
s++;
}var u=o.ownerDocument||o;
u.setProperty(f,
r.join(e));
}
try{return o.selectSingleNode(p);
}catch(err){}},
"default":function(o,
p,
q){if(!this.__lV){this.__lV=new XPathEvaluator();
}var v=this.__lV;
try{var w;
if(q){w=function(t){return q[t]||null;
};
}else{w=v.createNSResolver(o);
}return v.evaluate(p,
o,
w,
XPathResult.FIRST_ORDERED_NODE_TYPE,
null).singleNodeValue;
}catch(err){throw new Error("selectSingleNode: query: "+p+", element: "+o+", error: "+err);
}}}),
selectNodes:qx.core.Variant.select(a,
{"mshtml|opera":function(o,
p,
q){if(q){var r=[];
var s=0;
for(var t in q){r[s]=d+t+c+q[t]+b;
s++;
}var u=o.ownerDocument||o;
u.setProperty(f,
r.join(e));
}return o.selectNodes(p);
},
"default":function(o,
p,
q){var v=this.__lV;
if(!v){this.__lV=v=new XPathEvaluator();
}
try{var w;
if(q){w=function(t){return q[t]||null;
};
}else{w=v.createNSResolver(o);
}var x=v.evaluate(p,
o,
w,
XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
null);
}catch(err){throw new Error("selectNodes: query: "+p+", element: "+o+", error: "+err);
}var y=[];
for(var s=0;s<x.snapshotLength;s++){y[s]=x.snapshotItem(s);
}return y;
}}),
getElementsByTagNameNS:qx.core.Variant.select(a,
{"mshtml":function(o,
z,
A){var u=o.ownerDocument||o;
u.setProperty(k,
m);
u.setProperty(l,
h+z+g);
return qx.xml.Element.selectNodes(o,
n+A);
},
"default":function(o,
z,
A){return o.getElementsByTagNameNS(z,
A);
}}),
getSingleNodeText:function(o,
p,
q){var B=this.selectSingleNode(o,
p,
q);
return qx.dom.Node.getText(B);
}}});
})();
(function(){var a="qx.client",
b="'",
c="SelectionLanguage",
d="xmlns:ns='",
e="qx.xml.Element",
f="SelectionNamespaces",
g="XPath",
h='descendant-or-self::ns:';
qx.Class.define(e,
{statics:{serialize:function(j){if(qx.dom.Node.isDocument(j)){j=j.documentElement;
}
if(window.XMLSerializer){return (new XMLSerializer()).serializeToString(j);
}else{return j.xml||j.outerHTML;
}},
selectSingleNode:qx.core.Variant.select(a,
{"mshtml|opera":function(j,
k){return j.selectSingleNode(k);
},
"default":function(j,
k){if(!this.__gJ){this.__gJ=new XPathEvaluator();
}var l=this.__gJ;
try{return l.evaluate(k,
j,
l.createNSResolver(j),
XPathResult.FIRST_ORDERED_NODE_TYPE,
null).singleNodeValue;
}catch(err){throw new Error("selectSingleNode: query: "+k+", element: "+j+", error: "+err);
}}}),
selectNodes:qx.core.Variant.select(a,
{"mshtml|opera":function(j,
k){return j.selectNodes(k);
},
"default":function(j,
k){var l=this.__gJ;
if(!l){this.__gJ=l=new XPathEvaluator();
}
try{var m=l.evaluate(k,
j,
l.createNSResolver(j),
XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,
null);
}catch(err){throw new Error("selectNodes: query: "+k+", element: "+j+", error: "+err);
}var n=[];
for(var o=0;o<m.snapshotLength;o++){n[o]=m.snapshotItem(o);
}return n;
}}),
getElementsByTagNameNS:qx.core.Variant.select(a,
{"mshtml":function(j,
p,
q){var r=j.ownerDocument||j;
r.setProperty(c,
g);
r.setProperty(f,
d+p+b);
return qx.xml.Element.selectNodes(j,
h+q);
},
"default":function(j,
p,
q){return j.getElementsByTagNameNS(p,
q);
}}),
getSingleNodeText:function(j,
k){var s=this.selectSingleNode(j,
k);
return qx.dom.Node.getText(s);
}}});
})();
(function(){var a="",
b='</div>',
c="Up",
d="none",
f="keypress",
g='.qxconsole .messages{background:white;height:100%;width:100%;overflow:auto;}',
h="Enter",
i="px",
j='.qxconsole .messages .user-result{background:white}',
k='.qxconsole .messages .level-error{background:#FFE2D5}',
l="div",
m="user-command",
n='<div class="command">',
o='.qxconsole .command input:focus{outline:none;}',
p='.qxconsole .messages .type-key{color:#565656;font-style:italic}',
q='.qxconsole .messages .type-instance{color:#565656;font-weight:bold}',
r='.qxconsole .messages div{padding:0px 4px;}',
s='.qxconsole .messages .level-debug{background:white}',
t='.qxconsole .messages .type-class{color:#5F3E8A;font-weight:bold}',
u="DIV",
v='.qxconsole .messages .level-user{background:#E3EFE9}',
w='<div class="qxconsole">',
x="D",
y='.qxconsole .messages .type-map{color:#CC3E8A;font-weight:bold;}',
z='.qxconsole .messages .type-string{color:black;font-weight:normal;}',
A='.qxconsole .control a{text-decoration:none;color:black;}',
B='<div class="messages">',
C='.qxconsole .messages .type-boolean{color:#15BC91;font-weight:normal;}',
D='<input type="text"/>',
E="clear",
F='.qxconsole .command input{width:100%;border:0 none;font-family:Consolas,Monaco,monospace;font-size:11px;line-height:1.2;}',
G='.qxconsole .messages .type-array{color:#CC3E8A;font-weight:bold;}',
H='.qxconsole{z-index:10000;width:600px;height:300px;top:0px;right:0px;position:absolute;border-left:1px solid black;color:black;border-bottom:1px solid black;color:black;font-family:Consolas,Monaco,monospace;font-size:11px;line-height:1.2;}',
I='.qxconsole .command{background:white;padding:2px 4px;border-top:1px solid black;}',
J='.qxconsole .messages .user-command{color:blue}',
K="F7",
L="qx.log.appender.Console",
M='.qxconsole .messages .level-info{background:#DEEDFA}',
N="block",
O='.qxconsole .messages .level-warn{background:#FFF7D5}',
P='.qxconsole .messages .type-stringify{color:#565656;font-weight:bold}',
Q='.qxconsole .messages .user-error{background:#FFE2D5}',
R='.qxconsole .control{background:#cdcdcd;border-bottom:1px solid black;padding:4px 8px;}',
S='<div class="control"><a href="javascript:qx.log.appender.Console.clear()">Clear</a> | <a href="javascript:qx.log.appender.Console.toggle()">Hide</a></div>',
T=">>> ",
U="Down",
V='.qxconsole .messages .type-number{color:#155791;font-weight:normal;}';
qx.Class.define(L,
{statics:{init:function(){var W=[H,
R,
A,
g,
r,
J,
j,
Q,
s,
M,
O,
k,
v,
z,
V,
C,
G,
y,
p,
t,
q,
P,
I,
F,
o];
qx.bom.Stylesheet.createElement(W.join(a));
var X=[w,
S,
B,
b,
n,
D,
b,
b];
var Y=document.createElement(u);
Y.innerHTML=X.join(a);
var ba=Y.firstChild;
document.body.appendChild(Y.firstChild);
this.__gK=ba;
this.__gL=ba.childNodes[1];
this.__gM=ba.childNodes[2].firstChild;
this.__gR();
qx.log.Logger.register(this);
qx.core.ObjectRegistry.register(this);
},
dispose:function(){qx.event.Registration.removeListener(document.documentElement,
f,
this.__gS,
this);
qx.log.Logger.unregister(this);
},
clear:function(){this.__gL.innerHTML=a;
},
process:function(bb){this.__gL.appendChild(qx.log.appender.Util.toHtml(bb));
this.__gN();
},
__gN:function(){this.__gL.scrollTop=this.__gL.scrollHeight;
},
__gO:true,
toggle:function(){if(!this.__gK){this.init();
}else if(this.__gK.style.display==d){this.__gK.style.display=N;
this.__gL.scrollTop=this.__gL.scrollHeight;
}else{this.__gK.style.display=d;
}},
__gP:[],
execute:function(){var bc=this.__gM.value;
if(bc==a){return;
}
if(bc==E){return this.clear();
}var bd=document.createElement(l);
bd.innerHTML=qx.log.appender.Util.escapeHTML(T+bc);
bd.className=m;
this.__gP.push(bc);
this.__gQ=this.__gP.length;
this.__gL.appendChild(bd);
this.__gN();
try{var be=window.eval(bc);
}catch(ex){qx.log.Logger.error(ex);
}
if(be!==undefined){qx.log.Logger.debug(be);
}},
__gR:function(bf){this.__gL.style.height=(this.__gK.clientHeight-this.__gK.firstChild.offsetHeight-this.__gK.lastChild.offsetHeight)+i;
},
__gS:function(bf){var bg=bf.getKeyIdentifier();
if((bg==K)||(bg==x&&bf.isCtrlPressed())){this.toggle();
bf.preventDefault();
}if(!this.__gK){return;
}if(!qx.dom.Hierarchy.contains(this.__gK,
bf.getTarget())){return;
}if(bg==h&&this.__gM.value!=a){this.execute();
this.__gM.value=a;
}if(bg==c||bg==U){this.__gQ+=bg==c?-1:1;
this.__gQ=Math.min(Math.max(0,
this.__gQ),
this.__gP.length);
var bb=this.__gP[this.__gQ];
this.__gM.value=bb||a;
this.__gM.select();
}}},
defer:function(bh){qx.event.Registration.addListener(document.documentElement,
f,
bh.__gS,
bh);
}});
})();
(function(){var a="'>",
b="</span>",
c="<span class='type-",
d="</span> ",
e="",
f="[",
g=", ",
h="<span class='object'>",
k="&gt;",
l="<span class='object' title='Object instance with hash code: ",
m="string",
n="level-",
o="0",
p="&lt;",
q="<span class='offset'>",
r="}",
s="qx.log.appender.Util",
t="&amp;",
u="&#39;",
v="DIV",
w="]",
x="<span>",
y="&quot;",
z="<span class='type-key'>",
A="{",
B="</span>:<span class='type-",
C="</span>: ",
D="]</span>: ",
E="map",
F="?";
qx.Class.define(s,
{statics:{toHtml:function(G){var H=[];
var I,
J,
K,
L;
H.push(q,
this.formatOffset(G.offset),
d);
if(G.object){var M=qx.core.ObjectRegistry.fromHashCode(G.object);
if(M){H.push(l+M.$$hash+a,
M.classname,
f,
M.$$hash,
D);
}}else if(G.clazz){H.push(h+G.clazz.classname,
C);
}var N=G.items;
for(var O=0,
P=N.length;O<P;O++){I=N[O];
J=I.text;
if(J instanceof Array){var L=[];
for(var Q=0,
R=J.length;Q<R;Q++){K=J[Q];
if(typeof K===m){L.push(x+this.escapeHTML(K)+b);
}else if(K.key){L.push(z+K.key+B+K.type+a+this.escapeHTML(K.text)+b);
}else{L.push(c+K.type+a+this.escapeHTML(K.text)+b);
}}H.push(c+I.type+a);
if(I.type===E){H.push(A,
L.join(g),
r);
}else{H.push(f,
L.join(g),
w);
}H.push(b);
}else{H.push(c+I.type+a+this.escapeHTML(J)+d);
}}var S=document.createElement(v);
S.innerHTML=H.join(e);
S.className=n+G.level;
return S;
},
formatOffset:function(T,
U){var V=T.toString();
var W=(U||6)-V.length;
var X=e;
for(var O=0;O<W;O++){X+=o;
}return X+V;
},
escapeHTML:function(Y){return String(Y).replace(/[<>&"']/g,
this.__gT);
},
__gT:function(ba){var bb={"<":p,
">":k,
"&":t,
"'":u,
'"':y};
return bb[ba]||F;
}}});
})();
(function(){var a="qx.ui.core.MRemoteLayoutHandling";
qx.Mixin.define(a,
{members:{setLayout:function(b){return this.getChildrenContainer().setLayout(b);
},
getLayout:function(){return this.getChildrenContainer().getLayout();
}}});
})();
(function(){var a="resize",
b="Boolean",
c="nw-resize",
d="mouseup",
f="mousedown",
g="w-resize",
h="losecapture",
i="se-resize",
j="resize-frame",
k="ne-resize",
l="n-resize",
m="sw-resize",
n="mouseout",
o="__gU",
p="s-resize",
q="mousemove",
r="move",
s="maximized",
t="Integer",
u="e-resize",
v="qx.ui.core.MResizable";
qx.Mixin.define(v,
{construct:function(){this.addListener(f,
this.__hf,
this,
true);
this.addListener(d,
this.__hg,
this);
this.addListener(q,
this.__hi,
this);
this.addListener(n,
this.__hj,
this);
this.addListener(h,
this.__hh,
this);
},
properties:{resizable:{check:b,
init:true},
resizeAllEdges:{check:b,
init:true},
resizeSensitivity:{check:t,
init:5},
useResizeFrame:{check:b,
init:true}},
members:{__gU:null,
__gV:null,
__gW:null,
__gX:null,
__gY:null,
__ha:function(){var w=this.__gU;
if(!w){w=this.__gU=new qx.ui.core.Widget();
w.setAppearance(j);
w.exclude();
qx.core.Init.getApplication().getRoot().add(w);
}return w;
},
__hb:function(){var x=this.__gY;
var w=this.__ha();
w.setUserBounds(x.left,
x.top,
x.width,
x.height);
w.show();
w.setZIndex(this.getZIndex()+1);
},
__hc:function(y){var z=this.__gV;
var A=this.getSizeHint();
var B=this.__gY;
var C=B.width;
var D=B.height;
var E=B.left;
var F=B.top;
var G;
if(z&1||z&2){G=y.getDocumentTop()-this.__gX;
if(z&1){D-=G;
}else{D+=G;
}
if(D<A.minHeight){D=A.minHeight;
}else if(D>A.maxHeight){D=A.maxHeight;
}
if(z&1){F+=B.height-D;
}}if(z&4||z&8){G=y.getDocumentLeft()-this.__gW;
if(z&4){C-=G;
}else{C+=G;
}
if(C<A.minWidth){C=A.minWidth;
}else if(C>A.maxWidth){C=A.maxWidth;
}
if(z&4){E+=B.width-C;
}}return {left:E,
top:F,
width:C,
height:D};
},
__hd:{1:l,
2:p,
4:g,
8:u,
5:c,
6:m,
9:k,
10:i},
__he:function(y){if(!this.getResizable()){return;
}var H=this.getContentLocation();
var I=this.getResizeAllEdges();
var J=this.getResizeSensitivity();
var K=y.getDocumentLeft();
var L=y.getDocumentTop();
var z=0;
if(I&&Math.abs(H.top-L)<J){z+=1;
}else if(Math.abs(H.bottom-L)<J){z+=2;
}
if(I&&Math.abs(H.left-K)<J){z+=4;
}else if(Math.abs(H.right-K)<J){z+=8;
}this.__gV=z;
},
__hf:function(y){if(!this.__gV){return;
}this.addState(a);
this.capture();
this.__gW=y.getDocumentLeft();
this.__gX=y.getDocumentTop();
var M=this.getContainerLocation();
var x=this.getBounds();
this.__gY={top:M.top,
left:M.left,
width:x.width,
height:x.height};
if(this.getUseResizeFrame()){this.__hb();
}y.stop();
},
__hg:function(y){if(!this.__gV){return;
}if(this.getUseResizeFrame()){this.__ha().exclude();
}var x=this.__hc(y);
this.setWidth(x.width);
this.setHeight(x.height);
if(this.getResizeAllEdges()){this.setLayoutProperties({left:x.left,
top:x.top});
}this.__gV=0;
this.removeState(a);
this.resetCursor();
this.getApplicationRoot().resetGlobalCursor();
this.releaseCapture();
},
__hh:function(y){if(!this.__gV){return;
}this.resetCursor();
this.getApplicationRoot().resetGlobalCursor();
this.removeState(r);
if(this.getUseResizeFrame()){this.__ha().exclude();
}},
__hi:function(y){if(this.hasState(a)){var x=this.__hc(y);
if(this.getUseResizeFrame()){var w=this.__ha();
w.setUserBounds(x.left,
x.top,
x.width,
x.height);
}else{this.setWidth(x.width);
this.setHeight(x.height);
if(this.getResizeAllEdges()){this.setLayoutProperties({left:x.left,
top:x.top});
}}y.stop();
}else if(!this.hasState(s)){this.__he(y);
var z=this.__gV;
var N=this.getApplicationRoot();
if(z){var O=this.__hd[z];
this.setCursor(O);
N.setGlobalCursor(O);
}else if(this.getCursor()){this.resetCursor();
N.resetGlobalCursor();
}}},
__hj:function(y){if(this.getCursor()&&!this.hasState(a)){this.resetCursor();
this.getApplicationRoot().resetGlobalCursor();
}}},
destruct:function(){this._disposeObjects(o);
}});
})();
(function(){var a="move",
b="Boolean",
c="__hk",
d="mouseup",
f="mousedown",
g="losecapture",
h="__hm",
i="qx.ui.core.MMovable",
j="__hl",
k="mousemove",
l="maximized",
m="move-frame";
qx.Mixin.define(i,
{properties:{movable:{check:b,
init:true},
useMoveFrame:{check:b,
init:false}},
members:{__hk:null,
__hl:null,
__hm:null,
__hn:null,
__ho:null,
_activateMoveHandle:function(n){if(this.__hk){throw new Error("The move handle could not be redefined!");
}this.__hk=n;
n.addListener(f,
this._onMoveMouseDown,
this);
n.addListener(d,
this._onMoveMouseUp,
this);
n.addListener(k,
this._onMoveMouseMove,
this);
n.addListener(g,
this.__hs,
this);
},
__hp:function(){var o=this.__hl;
if(!o){o=this.__hl=new qx.ui.core.Widget();
o.setAppearance(m);
o.exclude();
qx.core.Init.getApplication().getRoot().add(o);
}return o;
},
__hq:function(){var p=this.getBounds();
var o=this.__hp();
o.setUserBounds(p.left,
p.top,
p.width,
p.height);
o.show();
o.setZIndex(this.getZIndex()+1);
},
__hr:function(q){var r=this.__hm;
var s=Math.max(r.left,
Math.min(r.right,
q.getDocumentLeft()));
var t=Math.max(r.top,
Math.min(r.bottom,
q.getDocumentTop()));
return {left:this.__hn+s,
top:this.__ho+t};
},
_onMoveMouseDown:function(q){if(!this.getMovable()||this.hasState(l)){return;
}var u=this.getLayoutParent();
var v=u.getContentLocation();
var w=u.getBounds();
this.__hm={left:v.left,
top:v.top,
right:v.left+w.width,
bottom:v.top+w.height};
var x=this.getContainerLocation();
this.__hn=x.left-q.getDocumentLeft();
this.__ho=x.top-q.getDocumentTop();
this.addState(a);
this.__hk.capture();
if(this.getUseMoveFrame()){this.__hq();
}q.stop();
},
_onMoveMouseMove:function(q){if(!this.hasState(a)){return;
}var y=this.__hr(q);
if(this.getUseMoveFrame()){this.__hp().setDomPosition(y.left,
y.top);
}else{this.setDomPosition(y.left,
y.top);
}},
_onMoveMouseUp:function(q){if(!this.hasState(a)){return;
}this.removeState(a);
this.__hk.releaseCapture();
var y=this.__hr(q);
this.setLayoutProperties({left:y.left,
top:y.top});
if(this.getUseMoveFrame()){this.__hp().exclude();
}},
__hs:function(q){if(!this.hasState(a)){return;
}this.removeState(a);
if(this.getUseMoveFrame()){this.__hp().exclude();
}}},
destruct:function(){this._disposeObjects(j,
c);
this._disposeFields(h);
}});
})();
(function(){var a="Integer",
b="_applyContentPadding",
c="resetPaddingRight",
d="setPaddingBottom",
e="resetPaddingTop",
f="qx.ui.core.MContentPadding",
g="resetPaddingLeft",
h="setPaddingTop",
i="setPaddingRight",
j="resetPaddingBottom",
k="contentPaddingLeft",
l="setPaddingLeft",
m="contentPaddingTop",
n="shorthand",
o="contentPaddingRight",
p="contentPaddingBottom";
qx.Mixin.define(f,
{properties:{contentPaddingTop:{check:a,
init:0,
apply:b,
themeable:true},
contentPaddingRight:{check:a,
init:0,
apply:b,
themeable:true},
contentPaddingBottom:{check:a,
init:0,
apply:b,
themeable:true},
contentPaddingLeft:{check:a,
init:0,
apply:b,
themeable:true},
contentPadding:{group:[m,
o,
p,
k],
mode:n,
themeable:true}},
members:{__ht:{contentPaddingTop:h,
contentPaddingRight:i,
contentPaddingBottom:d,
contentPaddingLeft:l},
__hu:{contentPaddingTop:e,
contentPaddingRight:c,
contentPaddingBottom:j,
contentPaddingLeft:g},
_applyContentPadding:function(q,
r,
s){var t=this._getContentPaddingTarget();
if(q==null){var u=this.__hu[s];
t[u]();
}else{var v=this.__ht[s];
t[v](q);
}}}});
})();
(function(){var a="qx.ui.window.IWindowManager";
qx.Interface.define(a,
{members:{setDesktop:function(b){this.assertInterface(b,
qx.ui.window.IDesktop);
},
changeActiveWindow:function(c,
d){},
updateStack:function(){},
bringToFront:function(e){this.assertInstance(e,
qx.ui.window.Window);
},
sendToBack:function(e){this.assertInstance(e,
qx.ui.window.Window);
}}});
})();
(function(){var a="__hv",
b="qx.ui.window.Manager";
qx.Class.define(b,
{extend:qx.core.Object,
implement:qx.ui.window.IWindowManager,
members:{__hv:null,
setDesktop:function(c){this.__hv=c;
this.updateStack();
},
changeActiveWindow:function(d,
e){this.bringToFront(d);
},
_minZIndex:1e5,
updateStack:function(){qx.ui.core.queue.Widget.add(this);
},
syncWidget:function(){this.__hv.unblockContent();
var f=this.__hv.getWindows();
var g=this._minZIndex-1;
var h=false;
var j,
k;
for(var m=0,
n=f.length;m<n;m++){j=f[m];
if(!j.isVisible()){continue;
}g+=2;
j.setZIndex(g);
if(j.getModal()){this.__hv.blockContent(g-1);
}h=h||j.isActive();
k=j;
}
if(!h&&k){k.setActive(true);
}},
bringToFront:function(j){var f=this.__hv.getWindows();
var o=qx.lang.Array.remove(f,
j);
if(o){f.push(j);
this.updateStack();
}},
sendToBack:function(j){var f=this.__hv.getWindows();
var o=qx.lang.Array.remove(f,
j);
if(o){f.unshift(j);
this.updateStack();
}}},
destruct:function(){this._disposeObjects(a);
}});
})();
(function(){var a="Boolean",
b="captionbar",
c="qx.event.type.Event",
d="maximized",
f="maximize-button",
g="_applyCaptionBarChange",
h="restore-button",
i="minimize-button",
j="close-button",
k="execute",
l="title",
m="icon",
n="pane",
o="statusbar",
p="statusbar-text",
q="mousedown",
r="String",
s="active",
t="beforeClose",
u="beforeMinimize",
v="changeStatus",
w="changeIcon",
x="excluded",
y="_applyCaption",
z="_applyActive",
A="beforeRestore",
B="minimize",
C="dblclick",
D="changeModal",
E="spacer",
F="_applyShowStatusbar",
G="click",
H="_applyStatus",
I="qx.ui.window.Window",
J="changeCaption",
K="mouseup",
L="_applyIcon",
M="beforeMaximize",
N="maximize",
O="restore",
P="window",
Q="close",
R="changeActive";
qx.Class.define(I,
{extend:qx.ui.core.Widget,
include:[qx.ui.core.MRemoteChildrenHandling,
qx.ui.core.MRemoteLayoutHandling,
qx.ui.core.MResizable,
qx.ui.core.MMovable,
qx.ui.core.MBlocker,
qx.ui.core.MContentPadding],
construct:function(S,
T){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.VBox());
this._createChildControl(b);
this._createChildControl(n);
if(T!=null){this.setIcon(T);
}
if(S!=null){this.setCaption(S);
}this._updateCaptionBar();
this.addListener(q,
this._onWindowEventStop);
this.addListener(K,
this._onWindowEventStop);
this.addListener(G,
this._onWindowEventStop);
this.addListener(q,
this._onWindowMouseDown,
this,
true);
qx.core.Init.getApplication().getRoot().add(this);
this.initVisibility();
qx.ui.core.FocusHandler.getInstance().addRoot(this);
},
statics:{DEFAULT_MANAGER_CLASS:qx.ui.window.Manager},
events:{"beforeClose":c,
"close":c,
"beforeMinimize":c,
"minimize":c,
"beforeMaximize":c,
"maximize":c,
"beforeRestore":c,
"restore":c},
properties:{appearance:{refine:true,
init:P},
visibility:{refine:true,
init:x},
focusable:{refine:true,
init:true},
active:{check:a,
init:false,
apply:z,
event:R},
modal:{check:a,
init:false,
event:D},
caption:{apply:y,
event:J,
nullable:true},
icon:{check:r,
nullable:true,
apply:L,
event:w,
themeable:true},
status:{check:r,
nullable:true,
apply:H,
event:v},
showClose:{check:a,
init:true,
apply:g,
themeable:true},
showMaximize:{check:a,
init:true,
apply:g,
themeable:true},
showMinimize:{check:a,
init:true,
apply:g,
themeable:true},
allowClose:{check:a,
init:true,
apply:g},
allowMaximize:{check:a,
init:true,
apply:g},
allowMinimize:{check:a,
init:true,
apply:g},
showStatusbar:{check:a,
init:false,
apply:F}},
members:{__hw:null,
__hx:null,
getChildrenContainer:function(){return this._getChildControl(n);
},
_forwardStates:{active:true,
maximized:true},
setLayoutParent:function(U){{};
arguments.callee.base.call(this,
U);
},
_createChildControlImpl:function(V){var W;
switch(V){case o:W=new qx.ui.container.Composite(new qx.ui.layout.HBox());
this._add(W);
W.add(this._getChildControl(p));
break;
case p:W=new qx.ui.basic.Label();
W.setContent(this.getStatus());
break;
case n:W=new qx.ui.container.Composite();
this._add(W,
{flex:1});
break;
case b:var X=new qx.ui.layout.Grid();
X.setColumnFlex(2,
1);
X.setRowFlex(0,
1);
W=new qx.ui.container.Composite(X);
this._add(W);
W.addListener(C,
this._onCaptionMouseDblClick,
this);
this._activateMoveHandle(W);
break;
case m:W=new qx.ui.basic.Image(this.getIcon());
this._getChildControl(b).add(W,
{row:0,
column:0});
break;
case l:W=new qx.ui.basic.Label(this.getCaption());
this._getChildControl(b).add(W,
{row:0,
column:1});
break;
case E:W=new qx.ui.core.Spacer();
this._getChildControl(b).add(W,
{row:0,
column:2});
break;
case i:W=new qx.ui.form.Button();
W.setFocusable(false);
W.addListener(k,
this._onMinimizeButtonClick,
this);
this._getChildControl(b).add(W,
{row:0,
column:3});
break;
case h:W=new qx.ui.form.Button();
W.setFocusable(false);
W.addListener(k,
this._onRestoreButtonClick,
this);
this._getChildControl(b).add(W,
{row:0,
column:4});
break;
case f:W=new qx.ui.form.Button();
W.setFocusable(false);
W.addListener(k,
this._onMaximizeButtonClick,
this);
this._getChildControl(b).add(W,
{row:0,
column:5});
break;
case j:W=new qx.ui.form.Button();
W.setFocusable(false);
W.addListener(k,
this._onCloseButtonClick,
this);
this._getChildControl(b).add(W,
{row:0,
column:6});
break;
}return W||arguments.callee.base.call(this,
V);
},
_updateCaptionBar:function(){var Y;
if(this.getIcon()){this._showChildControl(m);
}else{this._excludeChildControl(m);
}
if(this.getCaption()){this._showChildControl(l);
}else{this._excludeChildControl(l);
}
if(this.getShowMinimize()){this._showChildControl(i);
Y=this._getChildControl(i);
this.getAllowMinimize()?Y.resetEnabled():Y.setEnabled(false);
}else{this._excludeChildControl(i);
}
if(this.getShowMaximize()){if(this.hasState(d)){this._showChildControl(h);
this._excludeChildControl(f);
}else{this._showChildControl(f);
this._excludeChildControl(h);
}Y=this._getChildControl(f);
this.getAllowMaximize()?Y.resetEnabled():Y.setEnabled(false);
}else{this._excludeChildControl(f);
this._excludeChildControl(h);
}
if(this.getShowClose()){this._showChildControl(j);
Y=this._getChildControl(j);
this.getAllowClose()?Y.resetEnabled():Y.setEnabled(false);
}else{this._excludeChildControl(j);
}},
close:function(){if(this.fireNonBubblingEvent(t,
qx.event.type.Event,
[false,
true])){this.hide();
this.fireEvent(Q);
}},
open:function(){this.show();
this.setActive(true);
this.focus();
},
center:function(){var U=this.getLayoutParent();
if(U){var ba=U.getBounds();
if(ba){var bb=this.getSizeHint();
var bc=Math.round((ba.width-bb.width)/2);
var bd=Math.round((ba.height-bb.height)/2);
this.moveTo(bc,
bd);
return;
}}this.warn("Centering depends on parent bounds!");
},
maximize:function(){var U=this.getLayoutParent();
if(!U){return;
}
if(U.supportsMaximize()){if(this.fireNonBubblingEvent(M,
qx.event.type.Event,
[false,
true])){var be=this.getLayoutProperties();
this.__hx=be.left;
this.__hw=be.top;
this.setLayoutProperties({left:null,
top:null,
edge:0});
this.addState(d);
this._updateCaptionBar();
this.fireEvent(N);
}}},
minimize:function(){if(this.fireNonBubblingEvent(u,
qx.event.type.Event,
[false,
true])){this.hide();
this.fireEvent(B);
}},
restore:function(){if(!this.hasState(d)){return;
}
if(this.fireNonBubblingEvent(A,
qx.event.type.Event,
[false,
true])){var bc=this.__hx;
var bd=this.__hw;
this.setLayoutProperties({edge:null,
left:bc,
top:bd});
this.removeState(d);
this._updateCaptionBar();
this.fireEvent(O);
}},
moveTo:function(bc,
bd){if(this.hasState(d)){return;
}this.setLayoutProperties({left:bc,
top:bd});
},
_applyActive:function(bf,
bg){if(bg){this.removeState(s);
}else{this.addState(s);
}},
_getContentPaddingTarget:function(){return this._getChildControl(n);
},
_applyShowStatusbar:function(bf,
bg){if(bf){this._showChildControl(o);
}else{this._excludeChildControl(o);
}},
_applyCaptionBarChange:function(bf,
bg){this._updateCaptionBar();
},
_applyStatus:function(bf,
bg){var bh=this._getChildControl(p,
true);
if(bh){bh.setContent(bf);
}},
_applyCaption:function(bf,
bg){this._getChildControl(l).setContent(bf);
},
_applyIcon:function(bf,
bg){this._getChildControl(m).setSource(bf);
},
_onWindowEventStop:function(bi){bi.stopPropagation();
},
_onWindowMouseDown:function(bi){this.setActive(true);
},
_onCaptionMouseDblClick:function(bi){if(this.getAllowMaximize()){this.hasState(d)?this.restore():this.maximize();
}},
_onMinimizeButtonClick:function(bi){this.minimize();
this._getChildControl(i).reset();
},
_onRestoreButtonClick:function(bi){this.restore();
this._getChildControl(h).reset();
},
_onMaximizeButtonClick:function(bi){this.maximize();
this._getChildControl(f).reset();
},
_onCloseButtonClick:function(bi){this.close();
this._getChildControl(j).reset();
}}});
})();
(function(){var a="qx.ui.window.IDesktop";
qx.Interface.define(a,
{members:{setWindowManager:function(b){this.assertInterface(b,
qx.ui.window.IWindowManager);
},
getWindows:function(){},
supportsMaximize:function(){},
blockContent:function(c){this.assertInteger(c);
},
unblockContent:function(){}}});
})();
(function(){var a="left",
b="top",
c="_applyLayoutChange",
d="hAlign",
e="flex",
f="vAlign",
g="Integer",
h="__hA",
k="__hy",
m="minWidth",
n="width",
o="__hF",
p="minHeight",
q="__hz",
r="qx.ui.layout.Grid",
s="height",
t="__hC",
u="maxHeight",
v="maxWidth",
w="__hB",
z="__hG";
qx.Class.define(r,
{extend:qx.ui.layout.Abstract,
construct:function(A,
B){arguments.callee.base.call(this);
this.__hy=[];
this.__hz=[];
if(A){this.setSpacingX(A);
}
if(B){this.setSpacingY(B);
}},
properties:{spacingX:{check:g,
init:0,
apply:c},
spacingY:{check:g,
init:0,
apply:c}},
members:{__hA:null,
__hy:null,
__hz:null,
__hB:null,
__hC:null,
__hD:null,
__hE:null,
__hF:null,
__hG:null,
verifyLayoutProperty:null,
__hH:function(){var C=[];
var D=[];
var E=[];
var F=0;
var G=0;
var H=this._getLayoutChildren();
for(var I=0,
J=H.length;I<J;I++){var K=H[I];
var L=K.getLayoutProperties();
var M=L.row;
var N=L.column;
L.colSpan=L.colSpan||1;
L.rowSpan=L.rowSpan||1;
if(M==null||N==null){throw new Error("The layout properties 'row' and 'column' must be defined!");
}
if(C[M]&&C[M][N]){throw new Error("There is already a widget in this cell ("+M+", "+N+")");
}
for(var O=N;O<N+L.colSpan;O++){for(var P=M;P<M+L.rowSpan;P++){if(C[P]==undefined){C[P]=[];
}C[P][O]=K;
G=Math.max(G,
O);
F=Math.max(F,
P);
}}
if(L.rowSpan>1){E.push(K);
}
if(L.colSpan>1){D.push(K);
}}for(var P=0;P<=F;P++){if(C[P]==undefined){C[P]=[];
}}this.__hA=C;
this.__hB=D;
this.__hC=E;
this.__hD=F;
this.__hE=G;
delete this._invalidChildrenCache;
},
_setRowData:function(M,
Q,
R){var S=this.__hy[M];
if(!S){this.__hy[M]={};
this.__hy[M][Q]=R;
}else{S[Q]=R;
}},
_setColumnData:function(N,
Q,
R){var T=this.__hz[N];
if(!T){this.__hz[N]={};
this.__hz[N][Q]=R;
}else{T[Q]=R;
}},
setSpacing:function(U){this.setSpacingY(U);
this.setSpacingX(U);
},
setColumnAlign:function(N,
V,
W){{};
this._setColumnData(N,
d,
V);
this._setColumnData(N,
f,
W);
this._applyLayoutChange();
return this;
},
getColumnAlign:function(N){var T=this.__hz[N]||{};
return {vAlign:T.vAlign||b,
hAlign:T.hAlign||a};
},
setRowAlign:function(M,
V,
W){{};
this._setRowData(M,
d,
V);
this._setRowData(M,
f,
W);
this._applyLayoutChange();
return this;
},
getRowAlign:function(M){var S=this.__hy[M]||{};
return {vAlign:S.vAlign||b,
hAlign:S.hAlign||a};
},
getCellWidget:function(M,
N){if(this._invalidChildrenCache){this.__hH();
}return this.__hA[M][N]||null;
},
getCellAlign:function(M,
N){var W=b;
var V=a;
var S=this.__hy[M];
var T=this.__hz[N];
var X=this.__hA[M][N];
if(X){var Y={vAlign:X.getAlignY(),
hAlign:X.getAlignX()};
}else{Y={};
}if(Y.vAlign){W=Y.vAlign;
}else if(S&&S.vAlign){W=S.vAlign;
}else if(T&&T.vAlign){W=T.vAlign;
}if(Y.hAlign){V=Y.hAlign;
}else if(T&&T.hAlign){V=T.hAlign;
}else if(S&&S.hAlign){V=S.hAlign;
}return {vAlign:W,
hAlign:V};
},
setColumnFlex:function(N,
ba){this._setColumnData(N,
e,
ba);
this._applyLayoutChange();
return this;
},
getColumnFlex:function(N){var T=this.__hz[N]||{};
return T.flex!==undefined?T.flex:0;
},
setRowFlex:function(M,
ba){this._setRowData(M,
e,
ba);
this._applyLayoutChange();
return this;
},
getRowFlex:function(M){var S=this.__hy[M]||{};
var bb=S.flex!==undefined?S.flex:0;
return bb;
},
setColumnMaxWidth:function(N,
bc){this._setColumnData(N,
v,
bc);
this._applyLayoutChange();
return this;
},
getColumnMaxWidth:function(N){var T=this.__hz[N]||{};
return T.maxWidth!==undefined?T.maxWidth:Infinity;
},
setColumnWidth:function(N,
bd){this._setColumnData(N,
n,
bd);
this._applyLayoutChange();
return this;
},
getColumnWidth:function(N){var T=this.__hz[N]||{};
return T.width!==undefined?T.width:null;
},
setColumnMinWidth:function(N,
be){this._setColumnData(N,
m,
be);
this._applyLayoutChange();
return this;
},
getColumnMinWidth:function(N){var T=this.__hz[N]||{};
return T.minWidth||0;
},
setRowMaxHeight:function(M,
bf){this._setRowData(M,
u,
bf);
this._applyLayoutChange();
return this;
},
getRowMaxHeight:function(M){var S=this.__hy[M]||{};
return S.maxHeight||Infinity;
},
setRowHeight:function(M,
bg){this._setRowData(M,
s,
bg);
this._applyLayoutChange();
return this;
},
getRowHeight:function(M){var S=this.__hy[M]||{};
return S.height!==undefined?S.height:null;
},
setRowMinHeight:function(M,
bh){this._setRowData(M,
p,
bh);
this._applyLayoutChange();
return this;
},
getRowMinHeight:function(M){var S=this.__hy[M]||{};
return S.minHeight||0;
},
__hI:function(X){var bi=X.getSizeHint();
var bj=X.getMarginLeft()+X.getMarginRight();
var bk=X.getMarginTop()+X.getMarginBottom();
var bl={height:bi.height+bk,
width:bi.width+bj,
minHeight:bi.minHeight+bk,
minWidth:bi.minWidth+bj,
maxHeight:bi.maxHeight+bk,
maxWidth:bi.maxWidth+bj};
return bl;
},
_fixHeightsRowSpan:function(bm){var bn=this.getSpacingY();
for(var I=0,
J=this.__hC.length;I<J;I++){var X=this.__hC[I];
var bi=this.__hI(X);
var Y=X.getLayoutProperties();
var bo=Y.row;
var bp=bn*(Y.rowSpan-1);
var bq=bp;
var br={};
for(var bs=0;bs<Y.rowSpan;bs++){var M=Y.row+bs;
var bt=bm[M];
var bb=this.getRowFlex(M);
if(bb>0){br[M]={min:bt.minHeight,
value:bt.height,
max:bt.maxHeight,
flex:bb};
}bp+=bt.height;
bq+=bt.minHeight;
}if(bp<bi.height){var bu=qx.ui.layout.Util.computeFlexOffsets(br,
bi.height,
bp);
for(var bs=0;bs<Y.rowSpan;bs++){var bv=bu[bo+bs]?bu[bo+bs].offset:0;
bm[bo+bs].height+=bv;
}}if(bq<bi.minHeight){var bu=qx.ui.layout.Util.computeFlexOffsets(br,
bi.minHeight,
bq);
for(var bs=0;bs<Y.rowSpan;bs++){var bv=bu[bo+bs]?bu[bo+bs].offset:0;
bm[bo+bs].minHeight+=bv;
}}}},
_fixWidthsColSpan:function(bw){var bx=this.getSpacingX();
for(var I=0,
J=this.__hB.length;I<J;I++){var X=this.__hB[I];
var bi=this.__hI(X);
var Y=X.getLayoutProperties();
var by=Y.column;
var bz=bx*(Y.colSpan-1);
var bA=bz;
var bB={};
var bv;
for(var bs=0;bs<Y.colSpan;bs++){var bC=Y.column+bs;
var bD=bw[bC];
var bE=this.getColumnFlex(bC);
if(bE>0){bB[bC]={min:bD.minWidth,
value:bD.width,
max:bD.maxWidth,
flex:bE};
}bz+=bD.width;
bA+=bD.minWidth;
}if(bz<bi.width){var bF=qx.ui.layout.Util.computeFlexOffsets(bB,
bi.width,
bz);
for(var bs=0;bs<Y.colSpan;bs++){bv=bF[by+bs]?bF[by+bs].offset:0;
bw[by+bs].width+=bv;
}}if(bA<bi.minWidth){var bF=qx.ui.layout.Util.computeFlexOffsets(bB,
bi.minWidth,
bA);
for(var bs=0;bs<Y.colSpan;bs++){bv=bF[by+bs]?bF[by+bs].offset:0;
bw[by+bs].minWidth+=bv;
}}}},
_getRowHeights:function(){if(this.__hF!=null){return this.__hF;
}var bm=[];
var F=this.__hD;
var G=this.__hE;
for(var M=0;M<=F;M++){var bh=0;
var bg=0;
var bf=0;
for(var bC=0;bC<=G;bC++){var X=this.__hA[M][bC];
if(!X){continue;
}var bG=X.getLayoutProperties().rowSpan||0;
if(bG>1){continue;
}var bH=this.__hI(X);
if(this.getRowFlex(M)>0){bh=Math.max(bh,
bH.minHeight);
}else{bh=Math.max(bh,
bH.height);
}bg=Math.max(bg,
bH.height);
}var bh=Math.max(bh,
this.getRowMinHeight(M));
var bf=this.getRowMaxHeight(M);
if(this.getRowHeight(M)!==null){var bg=this.getRowHeight(M);
}else{var bg=Math.max(bh,
Math.min(bg,
bf));
}bm[M]={minHeight:bh,
height:bg,
maxHeight:bf};
}
if(this.__hC.length>0){this._fixHeightsRowSpan(bm);
}this.__hF=bm;
return bm;
},
_getColWidths:function(){if(this.__hG!=null){return this.__hG;
}var bw=[];
var G=this.__hE;
var F=this.__hD;
for(var bC=0;bC<=G;bC++){var bd=0;
var be=0;
var bc=Infinity;
for(var M=0;M<=F;M++){var X=this.__hA[M][bC];
if(!X){continue;
}var bI=X.getLayoutProperties().colSpan||0;
if(bI>1){continue;
}var bH=this.__hI(X);
if(this.getColumnFlex(bC)>0){be=Math.max(be,
bH.minWidth);
}else{be=Math.max(be,
bH.width);
}bd=Math.max(bd,
bH.width);
}var be=Math.max(be,
this.getColumnMinWidth(bC));
var bc=this.getColumnMaxWidth(bC);
if(this.getColumnWidth(bC)!==null){var bd=this.getColumnWidth(bC);
}else{var bd=Math.max(be,
Math.min(bd,
bc));
}bw[bC]={minWidth:be,
width:bd,
maxWidth:bc};
}
if(this.__hB.length>0){this._fixWidthsColSpan(bw);
}this.__hG=bw;
return bw;
},
_getColumnFlexOffsets:function(bd){var bi=this.getSizeHint();
var bJ=bd-bi.width;
if(bJ==0){return {};
}var bw=this._getColWidths();
var bK={};
for(var I=0,
J=bw.length;I<J;I++){var bC=bw[I];
var bE=this.getColumnFlex(I);
if((bE<=0)||(bC.width==bC.maxWidth&&bJ>0)||(bC.width==bC.minWidth&&bJ<0)){continue;
}bK[I]={min:bC.minWidth,
value:bC.width,
max:bC.maxWidth,
flex:bE};
}return qx.ui.layout.Util.computeFlexOffsets(bK,
bd,
bi.width);
},
_getRowFlexOffsets:function(bg){var bi=this.getSizeHint();
var bJ=bg-bi.height;
if(bJ==0){return {};
}var bm=this._getRowHeights();
var bK={};
for(var I=0,
J=bm.length;I<J;I++){var M=bm[I];
var bb=this.getRowFlex(I);
if((bb<=0)||(M.height==M.maxHeight&&bJ>0)||(M.height==M.minHeight&&bJ<0)){continue;
}bK[I]={min:M.minHeight,
value:M.height,
max:M.maxHeight,
flex:bb};
}return qx.ui.layout.Util.computeFlexOffsets(bK,
bg,
bi.height);
},
renderLayout:function(bL,
bM){if(this._invalidChildrenCache){this.__hH();
}var bN=qx.ui.layout.Util;
var bx=this.getSpacingX();
var bn=this.getSpacingY();
var bO=this._getColWidths();
var bP=this._getColumnFlexOffsets(bL);
var bw=[];
var G=this.__hE;
var F=this.__hD;
var bv;
for(var bC=0;bC<=G;bC++){bv=bP[bC]?bP[bC].offset:0;
bw[bC]=bO[bC].width+bv;
}var bQ=this._getRowHeights();
var bR=this._getRowFlexOffsets(bM);
var bm=[];
for(var M=0;M<=F;M++){bv=bR[M]?bR[M].offset:0;
bm[M]=bQ[M].height+bv;
}var bS=0;
for(var bC=0;bC<=G;bC++){var bT=0;
for(var M=0;M<=F;M++){var X=this.__hA[M][bC];
if(!X){bT+=bm[M]+bn;
continue;
}var Y=X.getLayoutProperties();
if(Y.row!==M||Y.column!==bC){bT+=bm[M]+bn;
continue;
}var bU=bx*(Y.colSpan-1);
for(var I=0;I<Y.colSpan;I++){bU+=bw[bC+I];
}var bV=bn*(Y.rowSpan-1);
for(var I=0;I<Y.rowSpan;I++){bV+=bm[M+I];
}var bW=X.getSizeHint();
var bX=X.getMarginTop();
var bY=X.getMarginLeft();
var ca=X.getMarginBottom();
var cb=X.getMarginRight();
var cc=Math.max(bW.minWidth,
Math.min(bU-bY-cb,
bW.maxWidth));
var cd=Math.max(bW.minHeight,
Math.min(bV-bX-ca,
bW.maxHeight));
var ce=this.getCellAlign(M,
bC);
var cf=bS+bN.computeHorizontalAlignOffset(ce.hAlign,
cc,
bU,
bY,
cb);
var cg=bT+bN.computeVerticalAlignOffset(ce.vAlign,
cd,
bV,
bX,
ca);
X.renderLayout(cf,
cg,
cc,
cd);
bT+=bm[M]+bn;
}bS+=bw[bC]+bx;
}},
invalidateLayoutCache:function(){arguments.callee.base.call(this);
this.__hG=null;
this.__hF=null;
},
_computeSizeHint:function(){if(this._invalidChildrenCache){this.__hH();
}var bw=this._getColWidths();
var be=0,
bd=0;
for(var I=0,
J=bw.length;I<J;I++){var bC=bw[I];
if(this.getColumnFlex(I)>0){be+=bC.minWidth;
}else{be+=bC.width;
}bd+=bC.width;
}var bm=this._getRowHeights();
var bh=0,
bg=0;
for(var I=0,
J=bm.length;I<J;I++){var M=bm[I];
if(this.getRowFlex(I)>0){bh+=M.minHeight;
}else{bh+=M.height;
}bg+=M.height;
}var A=this.getSpacingX()*(bw.length-1);
var B=this.getSpacingY()*(bm.length-1);
var bi={minWidth:be+A,
width:bd+A,
minHeight:bh+B,
height:bg+B};
return bi;
}},
destruct:function(){this._disposeFields(h,
k,
q,
w,
t,
z,
o);
}});
})();
(function(){var a="_applyLayoutChange",
b="left",
c="top",
d="Decorator",
e="Integer",
f="x",
g="y",
h="auto",
j="__hK",
k="qx.ui.layout.Dock",
l="__hJ",
m="_applySort",
n="west",
o="north",
p="south",
q="center",
r="east",
s="Boolean",
t="bottom",
u="right";
qx.Class.define(k,
{extend:qx.ui.layout.Abstract,
construct:function(v,
w,
x,
y){arguments.callee.base.call(this);
if(v){this.setSpacingX(v);
}
if(w){this.setSpacingY(w);
}
if(x){this.setSeparatorX(x);
}
if(y){this.setSeparatorY(y);
}},
properties:{sort:{check:[h,
g,
f],
init:h,
apply:m},
separatorX:{check:d,
nullable:true,
apply:a},
separatorY:{check:d,
nullable:true,
apply:a},
connectSeparators:{check:s,
init:false,
apply:a},
spacingX:{check:e,
init:0,
apply:a},
spacingY:{check:e,
init:0,
apply:a}},
members:{__hJ:null,
__hK:null,
verifyLayoutProperty:null,
_applySort:function(){this._invalidChildrenCache=true;
this._applyLayoutChange();
},
__hL:{north:1,
south:2,
west:3,
east:4,
center:5},
__hM:{1:c,
2:t,
3:b,
4:u},
__hN:function(){var z=this._getLayoutChildren();
var A,
B;
var C=z.length;
var D=[];
var E=[];
var F=[];
var G=this.getSort()===g;
var H=this.getSort()===f;
for(var I=0;I<C;I++){A=z[I];
F=A.getLayoutProperties().edge;
if(F===q){if(B){throw new Error("It is not allowed to have more than one child aligned to 'center'!");
}B=A;
}else if(H||G){if(F===o||F===p){G?D.push(A):E.push(A);
}else if(F===n||F===r){G?E.push(A):D.push(A);
}}else{D.push(A);
}}var J=D.concat(E);
if(B){J.push(B);
}this.__hJ=J;
var K=[];
for(var I=0;I<C;I++){F=J[I].getLayoutProperties().edge;
K[I]=this.__hL[F]||5;
}this.__hK=K;
delete this._invalidChildrenCache;
},
renderLayout:function(L,
M){if(this._invalidChildrenCache){this.__hN();
}var N=qx.ui.layout.Util;
var O=this.__hJ;
var K=this.__hK;
var C=O.length;
var P,
A,
Q,
R,
S,
T,
U,
V,
W;
var X=[];
var Y=[];
var ba=this._getSeparatorWidths();
var v=this.getSpacingX();
var w=this.getSpacingY();
var bb=-v;
var bc=-w;
if(ba.x){bb-=ba.x+v;
}
if(ba.y){bc-=ba.y+w;
}
for(var I=0;I<C;I++){A=O[I];
R=A.getLayoutProperties();
Q=A.getSizeHint();
U=Q.width;
V=Q.height;
if(R.width!=null){U=Math.floor(L*parseFloat(R.width)/100);
if(U<Q.minWidth){U=Q.minWidth;
}else if(U>Q.maxWidth){U=Q.maxWidth;
}}
if(R.height!=null){V=Math.floor(M*parseFloat(R.height)/100);
if(V<Q.minHeight){V=Q.minHeight;
}else if(V>Q.maxHeight){V=Q.maxHeight;
}}X[I]=U;
Y[I]=V;
switch(K[I]){case 1:case 2:bc+=V+A.getMarginTop()+A.getMarginBottom()+w;
if(ba.y){bc+=ba.y+w;
}break;
case 3:case 4:bb+=U+A.getMarginLeft()+A.getMarginRight()+v;
if(ba.x){bb+=ba.x+v;
}break;
default:bb+=U+A.getMarginLeft()+A.getMarginRight()+v;
bc+=V+A.getMarginTop()+A.getMarginBottom()+w;
if(ba.x){bb+=ba.x+v;
}
if(ba.y){bc+=ba.y+w;
}}}if(bb!=L){P={};
T=bb<L;
for(var I=0;I<C;I++){A=O[I];
switch(K[I]){case 3:case 4:case 5:S=A.getLayoutProperties().flex;
if(S==null&&K[I]==5){S=1;
}
if(S>0){Q=A.getSizeHint();
P[I]={min:Q.minWidth,
value:X[I],
max:Q.maxWidth,
flex:S};
}}}var J=N.computeFlexOffsets(P,
L,
bb);
for(var I in J){W=J[I].offset;
X[I]+=W;
bb+=W;
}}if(bc!=M){P=[];
T=bc<M;
for(var I=0;I<C;I++){A=O[I];
switch(K[I]){case 1:case 2:case 5:S=A.getLayoutProperties().flex;
if(S==null&&K[I]==5){S=1;
}
if(S>0){Q=A.getSizeHint();
P[I]={min:Q.minHeight,
value:Y[I],
max:Q.maxHeight,
flex:S};
}}}var J=N.computeFlexOffsets(P,
M,
bc);
for(var I in J){W=J[I].offset;
Y[I]+=W;
bc+=W;
}}this._clearSeparators();
var x=this.getSeparatorX(),
y=this.getSeparatorY();
var bd=this.getConnectSeparators();
var be=0,
bf=0;
var bg,
bh,
U,
V,
bi,
F;
var bj,
bk,
bl,
bm;
var bn,
bo,
bp,
bq;
var br=this.__hM;
for(var I=0;I<C;I++){A=O[I];
F=K[I];
Q=A.getSizeHint();
bn=A.getMarginTop();
bo=A.getMarginBottom();
bp=A.getMarginLeft();
bq=A.getMarginRight();
switch(F){case 1:case 2:U=L-bp-bq;
if(U<Q.minWidth){U=Q.minWidth;
}else if(U>Q.maxWidth){U=Q.maxWidth;
}V=Y[I];
bh=be+N.computeVerticalAlignOffset(br[F],
V,
M,
bn,
bo);
bg=bf+N.computeHorizontalAlignOffset(A.getAlignX()||b,
U,
L,
bp,
bq);
if(ba.y){if(F==1){bk=be+V+bn+w+bo;
}else{bk=be+M-V-bn-w-bo-ba.y;
}bj=bg;
bl=L;
if(bd&&bj>0){bj-=v+bp;
bl+=(v)*2;
}else{bj-=bp;
}this._renderSeparator(y,
{left:bj,
top:bk,
width:bl,
height:ba.y});
}bi=V+bn+bo+w;
if(ba.y){bi+=ba.y+w;
}M-=bi;
if(F==1){be+=bi;
}break;
case 3:case 4:V=M-bn-bo;
if(V<Q.minHeight){V=Q.minHeight;
}else if(V>Q.maxHeight){V=Q.maxHeight;
}U=X[I];
bg=bf+N.computeHorizontalAlignOffset(br[F],
U,
L,
bp,
bq);
bh=be+N.computeVerticalAlignOffset(A.getAlignY()||c,
V,
M,
bn,
bo);
if(ba.x){if(F==3){bj=bf+U+bp+v+bq;
}else{bj=bf+L-U-bp-v-bq-ba.x;
}bk=bh;
bm=M;
if(bd&&bk>0){bk-=w+bn;
bm+=(w)*2;
}else{bk-=bn;
}this._renderSeparator(x,
{left:bj,
top:bk,
width:ba.x,
height:bm});
}bi=U+bp+bq+v;
if(ba.x){bi+=ba.x+v;
}L-=bi;
if(F==3){bf+=bi;
}break;
default:U=L-bp-bq;
V=M-bn-bo;
if(U<Q.minWidth){U=Q.minWidth;
}else if(U>Q.maxWidth){U=Q.maxWidth;
}if(V<Q.minHeight){V=Q.minHeight;
}else if(V>Q.maxHeight){V=Q.maxHeight;
}bg=bf+N.computeHorizontalAlignOffset(A.getAlignX()||b,
U,
L,
bp,
bq);
bh=be+N.computeVerticalAlignOffset(A.getAlignY()||c,
V,
M,
bn,
bo);
}A.renderLayout(bg,
bh,
U,
V);
}},
_getSeparatorWidths:function(){var x=this.getSeparatorX(),
y=this.getSeparatorY();
if(x||y){var bs=qx.theme.manager.Decoration.getInstance();
}
if(x){var bt=bs.resolve(x);
var bu=bt.getInsets();
var bv=bu.left+bu.right;
}
if(y){var bw=bs.resolve(y);
var bx=bw.getInsets();
var by=bx.top+bx.bottom;
}return {x:bv||0,
y:by||0};
},
_computeSizeHint:function(){if(this._invalidChildrenCache){this.__hN();
}var O=this.__hJ;
var K=this.__hK;
var C=O.length;
var Q,
A;
var bz,
bA;
var bB=0,
bC=0;
var bD=0,
bE=0;
var bF=0,
bG=0;
var bH=0,
bI=0;
var ba=this._getSeparatorWidths();
var v=this.getSpacingX(),
w=this.getSpacingY();
var bJ=-v,
bK=-w;
if(ba.x){bJ-=ba.x+v;
}
if(ba.y){bK-=ba.y+w;
}for(var I=0;I<C;I++){A=O[I];
Q=A.getSizeHint();
bz=A.getMarginLeft()+A.getMarginRight();
bA=A.getMarginTop()+A.getMarginBottom();
switch(K[I]){case 1:case 2:bF=Math.max(bF,
Q.width+bB+bz);
bG=Math.max(bG,
Q.minWidth+bC+bz);
bH+=Q.height+bA;
bI+=Q.minHeight+bA;
bK+=w;
if(ba.y){bK+=ba.y+w;
}break;
case 3:case 4:bD=Math.max(bD,
Q.height+bH+bA);
bE=Math.max(bE,
Q.minHeight+bI+bA);
bB+=Q.width+bz;
bC+=Q.minWidth+bz;
bJ+=v;
if(ba.x){bJ+=ba.x+v;
}break;
default:bB+=Q.width+bz;
bC+=Q.minWidth+bz;
bH+=Q.height+bA;
bI+=Q.minHeight+bA;
bJ+=v;
if(ba.x){bJ+=ba.x+v;
}bK+=w;
if(ba.y){bK+=ba.y+w;
}}}var bL=Math.max(bC,
bG)+bJ;
var U=Math.max(bB,
bF)+bJ;
var bM=Math.max(bE,
bI)+bK;
var V=Math.max(bD,
bH)+bK;
return {minWidth:bL,
width:U,
minHeight:bM,
height:V};
}},
destruct:function(){this._disposeFields(j,
l);
}});
})();
(function(){var b="Unidentified",
c="+",
d="short",
f="keydown",
g="Control",
h="-",
j="__hP",
k="PageUp",
l="Escape",
m="Boolean",
n="qx.event.type.Data",
o="_applyShortcut",
p="PrintScreen",
q="NumLock",
r="5",
s="8",
t="execute",
u="Meta",
v="0",
w="PageDown",
x="__hO",
y="Shift",
z="You can only specify one non modifier key!",
A="3",
B="/",
C="Delete",
D="String",
E="changeEnabled",
F="*",
G="Not a valid key name for a command: ",
H="6",
I="4",
J="Alt",
K="2",
L="_applyEnabled",
M="1",
N="7",
O="qx.event.Command",
P="a",
Q="z",
R="9";
qx.Class.define(O,
{extend:qx.core.Object,
construct:function(S){arguments.callee.base.call(this);
this.__hO={};
this.__hP=null;
if(S!=null){this.setShortcut(S);
}{};
this.initEnabled();
},
events:{"execute":n},
properties:{enabled:{init:true,
check:m,
event:E,
apply:L},
shortcut:{check:D,
apply:o,
nullable:true}},
members:{execute:function(T){this.fireDataEvent(t,
T);
},
__hQ:function(U){if(this.getEnabled()&&this.matchesKeyEvent(U)){this.execute(U.getTarget());
U.preventDefault();
U.stopPropagation();
}},
_applyEnabled:function(V,
W){if(V){qx.event.Registration.addListener(document.documentElement,
f,
this.__hQ,
this);
}else{qx.event.Registration.removeListener(document.documentElement,
f,
this.__hQ,
this);
}},
_applyShortcut:function(V,
W){if(V){this.__hO={};
this.__hP=null;
var X=V.split(/[-+\s]+/);
var Y=X.length;
for(var ba=0;ba<Y;ba++){var bb=this.__hS(X[ba]);
switch(bb){case g:case y:case u:case J:this.__hO[bb]=true;
break;
case b:var bc=G+X[ba];
this.error(bc);
throw bc;
default:if(this.__hP){var bc=z;
this.error(bc);
throw bc;
}this.__hP=bb;
}}}return true;
},
matchesKeyEvent:function(bd){var be=this.__hP;
if(!be){return ;
}if((this.__hO.Shift&&!bd.isShiftPressed())||(this.__hO.Control&&!bd.isCtrlPressed())||(this.__hO.Meta&&!bd.isMetaPressed())||(this.__hO.Alt&&!bd.isAltPressed())){return false;
}
if(be==bd.getKeyIdentifier()){return true;
}return false;
},
__hR:{esc:l,
ctrl:g,
print:p,
del:C,
pageup:k,
pagedown:w,
numlock:q,
numpad_0:v,
numpad_1:M,
numpad_2:K,
numpad_3:A,
numpad_4:I,
numpad_5:r,
numpad_6:H,
numpad_7:N,
numpad_8:s,
numpad_9:R,
numpad_divide:B,
numpad_multiply:F,
numpad_minus:h,
numpad_plus:c},
__hS:function(bf){var bg=qx.event.handler.Keyboard;
var bh=b;
if(bg.isValidKeyIdentifier(bf)){return bf;
}
if(bf.length==1&&bf>=P&&bf<=Q){return bf.toUpperCase();
}bf=bf.toLowerCase();
var bh=this.__hR[bf]||qx.lang.String.firstUp(bf);
if(bg.isValidKeyIdentifier(bh)){return bh;
}else{return b;
}},
toString:function(){var be=this.__hP;
var bi=[];
for(var bj in this.__hO){bi.push(qx.locale.Key.getKeyName(d,
bj));
}
if(be){bi.push(qx.locale.Key.getKeyName(d,
be));
}return bi.join(c);
}},
destruct:function(){this.setEnabled(false);
this._disposeFields(x,
j);
}});
})();
(function(){var a="",
b="changeMenu",
c="commandId",
d="changeEnabled",
f="org.argeo.slc.web.event.Command",
g="execute";
qx.Class.define(f,
{extend:qx.event.Command,
properties:{id:{init:a},
label:{init:a},
icon:{init:a},
menu:{nullable:true,
event:b},
menuCallback:{nullable:true},
menuContext:{nullable:true}},
construct:function(h,
i,
j,
k){arguments.callee.base.call(this,
k);
this.setId(h);
this.setLabel(i);
this.setIcon(j);
},
members:{getMenuButton:function(){var l=new qx.ui.menu.Button(this.getLabel(),
this.getIcon(),
this,
this.getMenu());
this.addTooltip(l);
if(this.getMenu()){this.addListener(b,
function(m){this.setMenu(m.getData());
},
l);
}return l;
},
getToolbarButton:function(){var l;
if(this.getMenu()){l=new qx.ui.toolbar.MenuButton(this.getLabel(),
this.getIcon(),
this.getMenuClone());
this.addListener(b,
function(m){l.setMenu(this.getMenuClone());
},
this);
this.addListener(d,
function(n){this.setEnabled(n.getData());
},
l);
l.setEnabled(this.getEnabled());
}else{l=new qx.ui.toolbar.Button(this.getLabel(),
this.getIcon(),
this);
}this.addTooltip(l);
return l;
},
getMenuClone:function(){if(!this.menuClone){this.menuClone=new qx.ui.menu.Menu();
this.menuClone.setMinWidth(110);
}return this.menuClone;
},
clearMenus:function(){this.getMenu().removeAll();
this.getMenuClone().removeAll();
},
addSubMenuButton:function(i,
j,
o,
p){var l=new qx.ui.menu.Button(i,
j);
l.setUserData(c,
o);
l.addListener(g,
this.executeSubMenuCallback,
this);
if(p){p.add(l);
}else{this.getMenu().add(l);
this.addSubMenuButton(i,
j,
o,
this.menuClone);
}},
executeSubMenuCallback:function(m){var l=m.getTarget();
var q=this.getMenuCallback();
q=qx.lang.Function.bind(q,
this.getMenuContext()||this);
q(l.getUserData(c));
},
addTooltip:function(r){if(this.getShortcut()!=null){r.setToolTip(new qx.ui.tooltip.ToolTip(this.getShortcut()));
}}}});
})();
(function(){var a="Left",
b="Meta",
c="Pause",
d="End",
e="Down",
f="Home",
g="Apps",
h="Win",
i="Right",
j="Backspace",
k="Space",
l="Up",
m="Shift",
n="Enter",
o="Scroll",
p="Alt",
q="Escape",
r="key_full_Meta",
s="PrintScreen",
t="NumLock",
u="key_short_Alt",
v="key_short_Insert",
w="Del",
x="key_full_Enter",
y="key_full_Control",
z="qx.locale.Key",
A="Tabulator",
B="key_full_Space",
C="key_short_Meta",
D="key_short_PageUp",
E="key_short_Pause",
F="key_full_Down",
G="key_short_Apps",
H="key_short_Win",
I="key_full_Right",
J="key_short_Up",
K="key_full_PageDown",
L="key_full_Alt",
M="PgDn",
N="Esc",
O="key_full_Insert",
P="Ctrl",
Q="key_short_Space",
R="key_short_Backspace",
S="key_short_Home",
T="key_short_Down",
U="PgUp",
V="key_short_CapsLock",
W="PageUp",
X="key_full_Up",
Y="key_full_Home",
ba="key_full_Backspace",
bb="PageDown",
bc="CapsLock",
bd="Ins",
be="Control",
bf="key_short_PrintScreen",
bg="Tab",
bh="key_full_Apps",
bi="key_short_Tab",
bj="key_short_End",
bk="_",
bl="Caps",
bm="key_short_NumLock",
bn="Num",
bo="key_full_Scroll",
bp="key_short_Left",
bq="key_short_Scroll",
br="key_",
bs="key_full_Pause",
bt="key_short_Right",
bu="key_full_PrintScreen",
bv="key_full_Win",
bw="key_short_Shift",
bx="key_short_PageDown",
by="key_short_Enter",
bz="key_short_Control",
bA="Insert",
bB="key_short_Escape",
bC="key_full_Tab",
bD="Print",
bE="Delete",
bF="key_full_CapsLock",
bG="key_full_Escape",
bH="key_short_Delete",
bI="key_full_PageUp",
bJ="key_full_Shift",
bK="key_full_NumLock",
bL="key_full_Delete",
bM="key_full_End",
bN="key_full_Left";
qx.Class.define(z,
{statics:{getKeyName:function(bO,
bP,
bQ){{};
var bR=br+bO+bk+bP;
var bS=qx.locale.Manager.getInstance().translate(bR,
[],
bQ);
if(bS==bR){return qx.locale.Key._keyNames[bR]||bP;
}else{return bS;
}}},
defer:function(bT,
bU,
bV){var bW={};
var bX=qx.locale.Manager;
bW[bX.marktr(R)]=j;
bW[bX.marktr(bi)]=bg;
bW[bX.marktr(Q)]=k;
bW[bX.marktr(by)]=n;
bW[bX.marktr(bw)]=m;
bW[bX.marktr(bz)]=P;
bW[bX.marktr(u)]=p;
bW[bX.marktr(V)]=bl;
bW[bX.marktr(C)]=b;
bW[bX.marktr(bB)]=N;
bW[bX.marktr(bp)]=a;
bW[bX.marktr(J)]=l;
bW[bX.marktr(bt)]=i;
bW[bX.marktr(T)]=e;
bW[bX.marktr(D)]=U;
bW[bX.marktr(bx)]=M;
bW[bX.marktr(bj)]=d;
bW[bX.marktr(S)]=f;
bW[bX.marktr(v)]=bd;
bW[bX.marktr(bH)]=w;
bW[bX.marktr(bm)]=bn;
bW[bX.marktr(bf)]=bD;
bW[bX.marktr(bq)]=o;
bW[bX.marktr(E)]=c;
bW[bX.marktr(H)]=h;
bW[bX.marktr(G)]=g;
bW[bX.marktr(ba)]=j;
bW[bX.marktr(bC)]=A;
bW[bX.marktr(B)]=k;
bW[bX.marktr(x)]=n;
bW[bX.marktr(bJ)]=m;
bW[bX.marktr(y)]=be;
bW[bX.marktr(L)]=p;
bW[bX.marktr(bF)]=bc;
bW[bX.marktr(r)]=b;
bW[bX.marktr(bG)]=q;
bW[bX.marktr(bN)]=a;
bW[bX.marktr(X)]=l;
bW[bX.marktr(I)]=i;
bW[bX.marktr(F)]=e;
bW[bX.marktr(bI)]=W;
bW[bX.marktr(K)]=bb;
bW[bX.marktr(bM)]=d;
bW[bX.marktr(Y)]=f;
bW[bX.marktr(O)]=bA;
bW[bX.marktr(bL)]=bE;
bW[bX.marktr(bK)]=t;
bW[bX.marktr(bu)]=s;
bW[bX.marktr(bo)]=o;
bW[bX.marktr(bs)]=c;
bW[bX.marktr(bv)]=h;
bW[bX.marktr(bh)]=g;
bT._keyNames=bW;
}});
})();
(function(){var a="inherit",
b="toolbar-button",
c="keydown",
d="qx.ui.toolbar.Button",
e="keyup";
qx.Class.define(d,
{extend:qx.ui.form.Button,
construct:function(f,
g,
h){arguments.callee.base.call(this,
f,
g,
h);
this.removeListener(c,
this._onKeyDown);
this.removeListener(e,
this._onKeyUp);
},
properties:{appearance:{refine:true,
init:b},
show:{refine:true,
init:a},
focusable:{refine:true,
init:false}}});
})();
(function(){var a="qx.ui.popup.Popup",
b="visible",
c="excluded",
d="popup",
e="Boolean";
qx.Class.define(a,
{extend:qx.ui.container.Composite,
include:qx.ui.core.MPlacement,
construct:function(f){arguments.callee.base.call(this,
f);
qx.core.Init.getApplication().getRoot().add(this);
this.initVisibility();
},
properties:{appearance:{refine:true,
init:d},
visibility:{refine:true,
init:c},
autoHide:{check:e,
init:true}},
members:{_applyVisibility:function(g,
h){arguments.callee.base.call(this,
g,
h);
var i=qx.ui.popup.Manager.getInstance();
g===b?i.add(this):i.remove(this);
}},
destruct:function(){qx.ui.popup.Manager.getInstance().remove(this);
}});
})();
(function(){var a="atom",
b="Integer",
c="String",
d="qx.ui.tooltip.ToolTip",
e="_applyIcon",
f="tooltip",
g="_applyLabel";
qx.Class.define(d,
{extend:qx.ui.popup.Popup,
construct:function(h,
i){arguments.callee.base.call(this);
qx.ui.tooltip.Manager.getInstance();
this.setLayout(new qx.ui.layout.Grow);
this._createChildControl(a);
if(h!=null){this.setLabel(h);
}
if(i!=null){this.setIcon(i);
}},
properties:{appearance:{refine:true,
init:f},
showTimeout:{check:b,
init:1000,
themeable:true},
hideTimeout:{check:b,
init:4000,
themeable:true},
label:{check:c,
nullable:true,
apply:g},
icon:{check:c,
nullable:true,
apply:e,
themeable:true}},
members:{_createChildControlImpl:function(j){var k;
switch(j){case a:k=new qx.ui.basic.Atom;
this._add(k);
break;
}return k||arguments.callee.base.call(this,
j);
},
_applyIcon:function(l,
m){var n=this._getChildControl(a);
l==null?n.resetIcon:n.setIcon(l);
},
_applyLabel:function(l,
m){var n=this._getChildControl(a);
l==null?n.resetLabel():n.setLabel(l);
}}});
})();
(function(){var a="mousedown",
b="__hT",
c="blur",
d="singleton",
f="qx.ui.popup.Manager";
qx.Class.define(f,
{type:d,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this.__hT={};
var g=qx.core.Init.getApplication().getRoot();
g.addListener(a,
this.__hV,
this,
true);
qx.bom.Element.addListener(window,
c,
this.hideAll,
this);
},
members:{__hT:null,
add:function(h){{};
this.__hT[h.$$hash]=h;
this.__hU();
},
remove:function(h){{};
var i=this.__hT;
if(i){delete i[h.$$hash];
this.__hU();
}},
hideAll:function(){var i=this.__hT;
if(i){for(var j in i){i[j].exclude();
}}},
__hU:function(){var k=1e6;
var i=this.__hT;
for(var j in i){i[j].setZIndex(k++);
}},
__hV:function(l){var m=l.getTarget();
var i=this.__hT;
for(var j in i){var h=i[j];
if(!h.getAutoHide()||m==h||qx.ui.core.Widget.contains(h,
m)){continue;
}h.exclude();
}}},
destruct:function(){var g=qx.core.Init.getApplication().getRoot();
if(g){g.removeListener(a,
this.__hV,
this,
true);
}this._disposeMap(b);
}});
})();
(function(){var a="focusout",
b="interval",
c="mouseover",
d="mouseout",
f="focusin",
g="mousemove",
h="__hY",
i="qx.ui.tooltip.ToolTip",
j="__hW",
k="__hX",
l="_applyCurrent",
m="qx.ui.tooltip.Manager",
n="singleton";
qx.Class.define(m,
{type:n,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
var o=qx.core.Init.getApplication().getRoot();
o.addListener(c,
this.__id,
this,
true);
o.addListener(f,
this.__if,
this,
true);
this.__hW=new qx.event.Timer();
this.__hW.addListener(b,
this.__ia,
this);
this.__hX=new qx.event.Timer();
this.__hX.addListener(b,
this.__ib,
this);
this.__hY={left:0,
top:0};
},
properties:{current:{check:i,
nullable:true,
apply:l}},
members:{__hY:null,
__hX:null,
__hW:null,
_applyCurrent:function(p,
q){if(q&&qx.ui.core.Widget.contains(q,
p)){return;
}if(q){q.exclude();
this.__hW.stop();
this.__hX.stop();
}var o=qx.core.Init.getApplication().getRoot();
if(p){this.__hW.startWith(p.getShowTimeout());
o.addListener(d,
this.__ie,
this,
true);
o.addListener(a,
this.__ig,
this,
true);
o.addListener(g,
this.__ic,
this,
true);
}else{o.removeListener(d,
this.__ie,
this,
true);
o.removeListener(a,
this.__ig,
this,
true);
o.removeListener(g,
this.__ic,
this,
true);
}},
__ia:function(r){var s=this.getCurrent();
if(s){this.__hX.startWith(s.getHideTimeout());
s.placeToPoint(this.__hY);
s.show();
}this.__hW.stop();
},
__ib:function(r){var s=this.getCurrent();
if(s){s.exclude();
}this.__hX.stop();
this.resetCurrent();
},
__ic:function(r){var t=this.__hY;
t.left=r.getDocumentLeft();
t.top=r.getDocumentTop();
},
__id:function(r){var u=r.getTarget();
var v;
while(u!=null){var v=u.getToolTip();
if(v){break;
}u=u.getLayoutParent();
}if(v){this.setCurrent(v);
}},
__ie:function(r){var u=r.getTarget();
var w=r.getRelatedTarget();
var v=this.getCurrent();
if(v&&(w==v||qx.ui.core.Widget.contains(v,
w))){return;
}if(w&&u&&qx.ui.core.Widget.contains(u,
w)){return;
}if(v&&!w){this.setCurrent(null);
}else{this.resetCurrent();
}},
__if:function(r){var u=r.getTarget();
var v=u.getToolTip();
if(v!=null){this.setCurrent(v);
}},
__ig:function(r){var u=r.getTarget();
if(!u){return;
}var v=this.getCurrent();
if(v&&v==u.getToolTip()){this.setCurrent(null);
}}},
destruct:function(){var o=qx.core.Init.getApplication().getRoot();
if(o){o.addListener(c,
this.__id,
this,
true);
o.addListener(f,
this.__if,
this,
true);
}this._disposeObjects(j,
k);
this._disposeFields(h);
}});
})();
(function(){var a="qx.ui.layout.Grow";
qx.Class.define(a,
{extend:qx.ui.layout.Abstract,
members:{verifyLayoutProperty:null,
renderLayout:function(b,
c){var d=this._getLayoutChildren();
var e,
f,
g,
h;
for(var j=0,
k=d.length;j<k;j++){e=d[j];
f=e.getSizeHint();
g=b;
if(g<f.minWidth){g=f.minWidth;
}else if(g>f.maxWidth){g=f.maxWidth;
}h=c;
if(h<f.minHeight){h=f.minHeight;
}else if(h>f.maxHeight){h=f.maxHeight;
}e.renderLayout(0,
0,
g,
h);
}},
_computeSizeHint:function(){var d=this._getLayoutChildren();
var e,
f;
var m=0,
n=0;
for(var j=0,
k=d.length;j<k;j++){e=d[j];
f=e.getSizeHint();
m=Math.max(m,
f.width);
n=Math.max(n,
f.height);
}return {width:m,
height:n};
}}});
})();
(function(){var a="qx.ui.menubar.Button",
b="menubar-button";
qx.Class.define(a,
{extend:qx.ui.toolbar.MenuButton,
properties:{appearance:{refine:true,
init:b}}});
})();
(function(){var a="qx.ui.form.IRadioItem",
b="boolean",
c="qx.event.type.Data";
qx.Interface.define(a,
{extend:qx.ui.form.IFormElement,
events:{"changeChecked":c},
members:{setChecked:function(d){this.assertType(d,
b);
},
getChecked:function(){},
setGroup:function(d){this.assertInstance(d,
qx.ui.form.RadioGroup);
},
getGroup:function(){}}});
})();
(function(){var a="checked",
b="String",
c="_applyChecked",
d="qx.ui.form.RadioGroup",
f="Boolean",
g="changeName",
h="menu-radiobutton",
i="qx.ui.menu.RadioButton",
j="changeChecked",
k="changeValue",
l="_applyGroup";
qx.Class.define(i,
{extend:qx.ui.menu.AbstractButton,
implement:qx.ui.form.IRadioItem,
construct:function(m,
n){arguments.callee.base.call(this);
if(m!=null){this.setLabel(m);
}
if(n!=null){this.setMenu(n);
}},
properties:{appearance:{refine:true,
init:h},
value:{check:b,
nullable:true,
event:k},
name:{check:b,
nullable:true,
event:g},
group:{check:d,
nullable:true,
apply:l},
checked:{check:f,
init:false,
apply:c,
event:j}},
members:{_applyChecked:function(o,
p){o?this.addState(a):this.removeState(a);
},
_applyGroup:function(o,
p){if(p){p.remove(this);
}
if(o){o.add(this);
}},
_onMouseUp:function(q){if(q.isLeftPressed()){this.setChecked(true);
}},
_onKeyPress:function(q){this.setChecked(true);
}}});
})();
(function(){var a="Boolean",
b="changeValue",
c="changeChecked",
d="changeSelected",
f="__ih",
g="changeEnabled",
h="qx.ui.form.RadioGroup",
j="changeName",
k="qx.ui.form.IRadioItem",
m="_applySelected",
n="_applyName",
o="_applyEnabled",
p="String",
q="qx.event.type.Data";
qx.Class.define(h,
{extend:qx.core.Object,
implement:qx.ui.form.IFormElement,
construct:function(r){arguments.callee.base.call(this);
this.__ih=[];
if(r!=null){this.add.apply(this,
arguments);
}this.addListener(d,
this._onChangeSelected);
},
properties:{enabled:{check:a,
apply:o,
event:g},
selected:{nullable:true,
apply:m,
event:d,
check:k},
name:{check:p,
nullable:true,
apply:n,
event:j},
wrap:{check:a,
init:true}},
events:{"changeValue":q},
members:{__ih:null,
getItems:function(){return this.__ih;
},
select:function(s){this.setSelected(s);
},
setValue:function(t){var u=this.__ih;
var s;
for(var v=0,
w=u.length;v<w;v++){s=u[v];
if(s.getValue()==t){this.setSelected(s);
break;
}}},
getValue:function(){var x=this.getSelected();
return x?x.getValue():null;
},
add:function(r){var u=this.__ih;
var s;
for(var v=0,
w=arguments.length;v<w;v++){s=arguments[v];
if(s.getGroup()===this){continue;
}s.addListener(c,
this._onItemChangeChecked,
this);
u.push(s);
s.setGroup(this);
if(s.getChecked()){this.setSelected(s);
}}if(u.length>0&&!this.getSelected()){this.setSelected(u[0]);
}},
remove:function(s){if(s.getGroup()===this){qx.lang.Array.remove(this.__ih,
s);
s.resetGroup();
s.removeListener(c,
this._onItemChangeChecked,
this);
if(s.getChecked()){this.resetSelected();
}}},
_onItemChangeChecked:function(y){var s=y.getTarget();
if(s.getChecked()){this.setSelected(s);
}else if(this.getSelected()==s){this.resetSelected();
}},
_onChangeSelected:function(y){var s=y.getData();
var t=null;
if(s){t=s.getValue();
if(t==null){t=s.getLabel();
}}this.fireDataEvent(b,
t);
},
_applySelected:function(t,
z){if(z){z.setChecked(false);
}
if(t){t.setChecked(true);
}var A=z?z.getValue():null;
var B=t?t.getValue():null;
if(A!=B){this.fireNonBubblingEvent(b,
qx.event.type.Data,
[B,
A]);
}},
_applyEnabled:function(t,
z){var u=this.__ih;
if(t==null){for(var v=0,
w=u.length;v<w;v++){u[v].resetEnabled();
}}else{for(var v=0,
w=u.length;v<w;v++){u[v].setEnabled(true);
}}},
_applyName:function(t,
z){var u=this.__ih;
if(t==null){for(var v=0,
w=u.length;v<w;v++){u[v].resetName();
}}else{for(var v=0,
w=u.length;v<w;v++){u[v].setName(t);
}}},
selectNext:function(){var s=this.getSelected();
var u=this.__ih;
var C=u.indexOf(s);
if(C==-1){return;
}var v=0;
var D=u.length;
if(this.getWrap()){C=(C+1)%D;
}else{C=Math.min(C+1,
D-1);
}
while(v<D&&!u[C].getEnabled()){C=(C+1)%D;
v++;
}this.setSelected(u[C]);
},
selectPrevious:function(){var s=this.getSelected();
var u=this.__ih;
var C=u.indexOf(s);
if(C==-1){return;
}var v=0;
var D=u.length;
if(this.getWrap()){C=(C-1+D)%D;
}else{C=Math.max(C-1,
0);
}
while(v<D&&!u[C].getEnabled()){C=(C-1+D)%D;
v++;
}this.setSelected(u[C]);
}},
destruct:function(){this._disposeArray(f);
}});
})();
(function(){var a="listener",
b="execute",
c="failed",
d="org.argeo.slc.web.util.RequestManager",
f="aborted",
g="timeout",
h="singleton";
qx.Class.define(d,
{type:h,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
},
members:{setStopCommand:function(i){this.command=i;
},
getRequest:function(j,
k,
l){var m=new qx.io.remote.Request(j,
k,
l);
this.enableCommand(m);
m.addListener(g,
function(n){this.requestTimeout(m);
},
this);
m.addListener(c,
function(n){this.requestFailed(m);
},
this);
m.addListener(f,
function(n){this.requestFailed(m);
},
this);
return m;
},
requestCreated:function(o){this.enableCommand(o);
},
requestAborted:function(o){this.disableCommand(o);
},
requestFailed:function(o){this.disableCommand(o);
},
requestTimeout:function(o){this.disableCommand(o);
},
requestCompleted:function(m){this.disableCommand(m);
},
disableCommand:function(m){this.command.setEnabled(false);
var p=m.getUserData(a);
if(p){this.command.removeListener(b,
p);
}},
enableCommand:function(m){this.command.setEnabled(true);
qx.ui.core.queue.Manager.flush();
var p=m.abort;
m.setUserData(a,
p);
this.command.addListener(b,
p);
}}});
})();
(function(){var a="Boolean",
b="qx.event.type.Event",
c="queued",
d="String",
f="sending",
g="qx.io.remote.Response",
h="receiving",
i="aborted",
j="failed",
k="completed",
l="configured",
m="timeout",
n="GET",
o="Pragma",
p="nocache",
q="POST",
r="no-cache",
s="Cache-Control",
t="Content-Type",
u="text/plain",
v="application/xml",
w="application/json",
x="text/html",
y="application/x-www-form-urlencoded",
z="qx.io.remote.Exchange",
A="Integer",
B="X-Qooxdoo-Response-Type",
C="_formFields",
D="HEAD",
E="qx.io.remote.Request",
F="_parameters",
G="_applyResponseType",
H="_applyState",
I="text/javascript",
J="changeState",
K="PUT",
L="_applyProhibitCaching",
M="",
N="_requestHeaders",
O="_applyMethod",
P="DELETE";
qx.Class.define(E,
{extend:qx.core.Object,
construct:function(Q,
R,
S){arguments.callee.base.call(this);
this._requestHeaders={};
this._parameters={};
this._formFields={};
if(Q!==undefined){this.setUrl(Q);
}
if(R!==undefined){this.setMethod(R);
}
if(S!==undefined){this.setResponseType(S);
}this.setProhibitCaching(true);
this._seqNum=++qx.io.remote.Request._seqNum;
},
events:{"created":b,
"configured":b,
"sending":b,
"receiving":b,
"completed":g,
"aborted":g,
"failed":g,
"timeout":g},
statics:{_seqNum:0},
properties:{url:{check:d,
init:M},
method:{check:[n,
q,
K,
D,
P],
apply:O,
init:n},
asynchronous:{check:a,
init:true},
data:{check:d,
nullable:true},
username:{check:d,
nullable:true},
password:{check:d,
nullable:true},
state:{check:[l,
c,
f,
h,
k,
i,
m,
j],
init:l,
apply:H,
event:J},
responseType:{check:[u,
I,
w,
v,
x],
init:u,
apply:G},
timeout:{check:A,
nullable:true},
prohibitCaching:{check:a,
init:true,
apply:L},
crossDomain:{check:a,
init:false},
fileUpload:{check:a,
init:false},
transport:{check:z,
nullable:true},
useBasicHttpAuth:{check:a,
init:false}},
members:{send:function(){qx.io.remote.RequestQueue.getInstance().add(this);
},
abort:function(){qx.io.remote.RequestQueue.getInstance().abort(this);
},
reset:function(){switch(this.getState()){case f:case h:this.error("Aborting already sent request!");
case c:this.abort();
break;
}},
isConfigured:function(){return this.getState()===l;
},
isQueued:function(){return this.getState()===c;
},
isSending:function(){return this.getState()===f;
},
isReceiving:function(){return this.getState()===h;
},
isCompleted:function(){return this.getState()===k;
},
isAborted:function(){return this.getState()===i;
},
isTimeout:function(){return this.getState()===m;
},
isFailed:function(){return this.getState()===j;
},
__ii:function(T){var U=T.clone();
U.setTarget(this);
this.dispatchEvent(U);
},
_onqueued:function(T){this.setState(c);
this.__ii(T);
},
_onsending:function(T){this.setState(f);
this.__ii(T);
},
_onreceiving:function(T){this.setState(h);
this.__ii(T);
},
_oncompleted:function(T){this.setState(k);
this.__ii(T);
this.dispose();
},
_onaborted:function(T){this.setState(i);
this.__ii(T);
this.dispose();
},
_ontimeout:function(T){this.setState(m);
this.__ii(T);
this.dispose();
},
_onfailed:function(T){this.setState(j);
this.__ii(T);
this.dispose();
},
_applyState:function(V,
W){{};
},
_applyProhibitCaching:function(V,
W){if(V){this.setParameter(p,
new Date().valueOf());
this.setRequestHeader(o,
r);
this.setRequestHeader(s,
r);
}else{this.removeParameter(p);
this.removeRequestHeader(o);
this.removeRequestHeader(s);
}},
_applyMethod:function(V,
W){if(V===q){this.setRequestHeader(t,
y);
}else{this.removeRequestHeader(t);
}},
_applyResponseType:function(V,
W){this.setRequestHeader(B,
V);
},
setRequestHeader:function(X,
Y){this._requestHeaders[X]=Y;
},
removeRequestHeader:function(X){delete this._requestHeaders[X];
},
getRequestHeader:function(X){return this._requestHeaders[X]||null;
},
getRequestHeaders:function(){return this._requestHeaders;
},
setParameter:function(X,
Y){this._parameters[X]=Y;
},
removeParameter:function(X){delete this._parameters[X];
},
getParameter:function(X){return this._parameters[X]||null;
},
getParameters:function(){return this._parameters;
},
setFormField:function(X,
Y){this._formFields[X]=Y;
},
removeFormField:function(X){delete this._formFields[X];
},
getFormField:function(X){return this._formFields[X]||null;
},
getFormFields:function(){return this._formFields;
},
getSequenceNumber:function(){return this._seqNum;
}},
destruct:function(){this.setTransport(null);
this._disposeFields(N,
F,
C);
}});
})();
(function(){var a="Integer",
b="sending",
c="failed",
d="timeout",
f="completed",
g="aborted",
h="_active",
j="_queue",
k="_applyEnabled",
l="Boolean",
m="interval",
n="qx.io.remote.RequestQueue",
o="_timer",
p="queued",
q="receiving",
r="singleton";
qx.Class.define(n,
{type:r,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this._queue=[];
this._active=[];
this._totalRequests=0;
this._timer=new qx.event.Timer(500);
this._timer.addListener(m,
this._oninterval,
this);
},
properties:{enabled:{init:true,
check:l,
apply:k},
maxTotalRequests:{check:a,
nullable:true},
maxConcurrentRequests:{check:a,
init:3},
defaultTimeout:{check:a,
init:5000}},
members:{_debug:function(){var s;
},
_check:function(){this._debug();
if(this._active.length==0&&this._queue.length==0){this._timer.stop();
}if(!this.getEnabled()){return;
}if(this._active.length>=this.getMaxConcurrentRequests()||this._queue.length==0){return;
}if(this.getMaxTotalRequests()!=null&&this._totalRequests>=this.getMaxTotalRequests()){return;
}var t=this._queue.shift();
var u=new qx.io.remote.Exchange(t);
this._totalRequests++;
this._active.push(u);
this._debug();
u.addListener(b,
t._onsending,
t);
u.addListener(q,
t._onreceiving,
t);
u.addListener(f,
t._oncompleted,
t);
u.addListener(g,
t._onaborted,
t);
u.addListener(d,
t._ontimeout,
t);
u.addListener(c,
t._onfailed,
t);
u.addListener(b,
this._onsending,
this);
u.addListener(f,
this._oncompleted,
this);
u.addListener(g,
this._oncompleted,
this);
u.addListener(d,
this._oncompleted,
this);
u.addListener(c,
this._oncompleted,
this);
u._start=(new Date).valueOf();
u.send();
if(this._queue.length>0){this._check();
}},
_remove:function(u){qx.lang.Array.remove(this._active,
u);
u.dispose();
this._check();
},
_activeCount:0,
_onsending:function(v){{};
},
_oncompleted:function(v){{};
this._remove(v.getTarget());
},
_oninterval:function(v){var w=this._active;
if(w.length==0){this._timer.stop();
return;
}var x=(new Date).valueOf();
var u;
var t;
var y=this.getDefaultTimeout();
var z;
var A;
for(var B=w.length-1;B>=0;B--){u=w[B];
t=u.getRequest();
if(t.isAsynchronous()){z=t.getTimeout();
if(z==0){continue;
}
if(z==null){z=y;
}A=x-u._start;
if(A>z){this.warn("Timeout: transport "+u.toHashCode());
this.warn(A+"ms > "+z+"ms");
u.timeout();
}}}},
_applyEnabled:function(C,
D){if(C){this._check();
}this._timer.setEnabled(C);
},
add:function(t){t.setState(p);
this._queue.push(t);
this._check();
if(this.getEnabled()){this._timer.start();
}},
abort:function(t){var u=t.getTransport();
if(u){u.abort();
}else if(qx.lang.Array.contains(this._queue,
t)){qx.lang.Array.remove(this._queue,
t);
}}},
destruct:function(){this._disposeArray(h);
this._disposeObjects(o);
this._disposeFields(j);
}});
})();
(function(){var a="sending",
b="completed",
c="receiving",
d="aborted",
f="failed",
g="timeout",
h="qx.io.remote.Response",
j="Connection dropped",
k="configured",
m="qx.event.type.Event",
n="Proxy authentication required",
o="qx.io.remote.transport.Abstract",
p="MSHTML-specific HTTP status code",
q="Not available",
r="Precondition failed",
s="Server error",
t="Moved temporarily",
u="qx.io.remote.Exchange",
v="Bad gateway",
w="Gone",
x="See other",
y="Partial content",
z="Server timeout",
A="qx.io.remote.transport.Script",
B="HTTP version not supported",
C="Unauthorized",
D="Multiple choices",
E="Payment required",
F="Not implemented",
G="Request-URL too large",
H="Length required",
I="_applyState",
J="changeState",
K="Not modified",
L="qx.io.remote.Request",
M="Connection closed by server",
N="Moved permanently",
O="_applyImplementation",
P="Method not allowed",
Q="Forbidden",
R="Use proxy",
S="Ok",
T="Not found",
U="Not acceptable",
V="Request time-out",
W="Bad request",
X="Conflict",
Y="No content",
ba="qx.io.remote.transport.XmlHttp",
bb="qx.io.remote.transport.Iframe",
bc="Request entity too large",
bd="Unknown status code",
be="Unsupported media type",
bf="Gateway time-out",
bg="created",
bh="Out of resources",
bi="undefined";
qx.Class.define(u,
{extend:qx.core.Object,
construct:function(bj){arguments.callee.base.call(this);
this.setRequest(bj);
bj.setTransport(this);
},
events:{"sending":m,
"receiving":m,
"completed":h,
"aborted":h,
"failed":h,
"timeout":h},
statics:{typesOrder:[ba,
bb,
A],
typesReady:false,
typesAvailable:{},
typesSupported:{},
registerType:function(bk,
bl){qx.io.remote.Exchange.typesAvailable[bl]=bk;
},
initTypes:function(){if(qx.io.remote.Exchange.typesReady){return;
}
for(var bl in qx.io.remote.Exchange.typesAvailable){var bm=qx.io.remote.Exchange.typesAvailable[bl];
if(bm.isSupported()){qx.io.remote.Exchange.typesSupported[bl]=bm;
}}qx.io.remote.Exchange.typesReady=true;
if(qx.lang.Object.isEmpty(qx.io.remote.Exchange.typesSupported)){throw new Error("No supported transport types were found!");
}},
canHandle:function(bn,
bo,
bp){if(!qx.lang.Array.contains(bn.handles.responseTypes,
bp)){return false;
}
for(var bq in bo){if(!bn.handles[bq]){return false;
}}return true;
},
_nativeMap:{0:bg,
1:k,
2:a,
3:c,
4:b},
wasSuccessful:function(br,
bs,
bt){if(bt){switch(br){case null:case 0:return true;
case -1:return bs<4;
default:return typeof br===bi;
}}else{switch(br){case -1:{};
return bs<4;
case 200:case 304:return true;
case 201:case 202:case 203:case 204:case 205:return true;
case 206:{};
return bs!==4;
case 300:case 301:case 302:case 303:case 305:case 400:case 401:case 402:case 403:case 404:case 405:case 406:case 407:case 408:case 409:case 410:case 411:case 412:case 413:case 414:case 415:case 500:case 501:case 502:case 503:case 504:case 505:{};
return false;
case 12002:case 12007:case 12029:case 12030:case 12031:case 12152:case 13030:{};
return false;
default:if(br>206&&br<300){return true;
}qx.log.Logger.debug(this,
"Unknown status code: "+br+" ("+bs+")");
return false;
}}},
statusCodeToString:function(br){switch(br){case -1:return q;
case 200:return S;
case 304:return K;
case 206:return y;
case 204:return Y;
case 300:return D;
case 301:return N;
case 302:return t;
case 303:return x;
case 305:return R;
case 400:return W;
case 401:return C;
case 402:return E;
case 403:return Q;
case 404:return T;
case 405:return P;
case 406:return U;
case 407:return n;
case 408:return V;
case 409:return X;
case 410:return w;
case 411:return H;
case 412:return r;
case 413:return bc;
case 414:return G;
case 415:return be;
case 500:return s;
case 501:return F;
case 502:return v;
case 503:return bh;
case 504:return bf;
case 505:return B;
case 12002:return z;
case 12029:return j;
case 12030:return j;
case 12031:return j;
case 12152:return M;
case 13030:return p;
default:return bd;
}}},
properties:{request:{check:L,
nullable:true},
implementation:{check:o,
nullable:true,
apply:O},
state:{check:[k,
a,
c,
b,
d,
g,
f],
init:k,
event:J,
apply:I}},
members:{send:function(){var bj=this.getRequest();
if(!bj){return this.error("Please attach a request object first");
}qx.io.remote.Exchange.initTypes();
var bu=qx.io.remote.Exchange.typesOrder;
var bv=qx.io.remote.Exchange.typesSupported;
var bp=bj.getResponseType();
var bo={};
if(bj.getAsynchronous()){bo.asynchronous=true;
}else{bo.synchronous=true;
}
if(bj.getCrossDomain()){bo.crossDomain=true;
}
if(bj.getFileUpload()){bo.fileUpload=true;
}for(var bw in bj.getFormFields()){bo.programaticFormFields=true;
break;
}var bx,
by;
for(var bz=0,
bA=bu.length;bz<bA;bz++){bx=bv[bu[bz]];
if(bx){if(!qx.io.remote.Exchange.canHandle(bx,
bo,
bp)){continue;
}
try{{};
by=new bx;
this.setImplementation(by);
by.setUseBasicHttpAuth(bj.getUseBasicHttpAuth());
by.send();
return true;
}catch(ex){this.error("Request handler throws error");
this.error(ex);
return;
}}}this.error("There is no transport implementation available to handle this request: "+bj);
},
abort:function(){var bB=this.getImplementation();
if(bB){{};
bB.abort();
}else{{};
this.setState(d);
}},
timeout:function(){var bB=this.getImplementation();
if(bB){this.warn("Timeout: implementation "+bB.toHashCode());
bB.timeout();
}else{this.warn("Timeout: forcing state to timeout");
this.setState(g);
}if(this.getRequest()){this.getRequest().setTimeout(0);
}},
_onsending:function(bC){this.setState(a);
},
_onreceiving:function(bC){this.setState(c);
},
_oncompleted:function(bC){this.setState(b);
},
_onabort:function(bC){this.setState(d);
},
_onfailed:function(bC){this.setState(f);
},
_ontimeout:function(bC){this.setState(g);
},
_applyImplementation:function(bD,
bE){if(bE){bE.removeListener(a,
this._onsending,
this);
bE.removeListener(c,
this._onreceiving,
this);
bE.removeListener(b,
this._oncompleted,
this);
bE.removeListener(d,
this._onabort,
this);
bE.removeListener(g,
this._ontimeout,
this);
bE.removeListener(f,
this._onfailed,
this);
}
if(bD){var bj=this.getRequest();
bD.setUrl(bj.getUrl());
bD.setMethod(bj.getMethod());
bD.setAsynchronous(bj.getAsynchronous());
bD.setUsername(bj.getUsername());
bD.setPassword(bj.getPassword());
bD.setParameters(bj.getParameters());
bD.setFormFields(bj.getFormFields());
bD.setRequestHeaders(bj.getRequestHeaders());
bD.setData(bj.getData());
bD.setResponseType(bj.getResponseType());
bD.addListener(a,
this._onsending,
this);
bD.addListener(c,
this._onreceiving,
this);
bD.addListener(b,
this._oncompleted,
this);
bD.addListener(d,
this._onabort,
this);
bD.addListener(g,
this._ontimeout,
this);
bD.addListener(f,
this._onfailed,
this);
}},
_applyState:function(bD,
bE){{};
switch(bD){case a:this.fireEvent(a);
break;
case c:this.fireEvent(c);
break;
case b:case d:case g:case f:var bn=this.getImplementation();
if(!bn){break;
}
if(this.hasListener(bD)){var bF=qx.event.Registration.createEvent(bD,
qx.io.remote.Response);
if(bD==b){var bG=bn.getResponseContent();
bF.setContent(bG);
if(bG===null){{};
bD=f;
}}bF.setStatusCode(bn.getStatusCode());
bF.setResponseHeaders(bn.getResponseHeaders());
this.dispatchEvent(bF);
}this.setImplementation(null);
bn.dispose();
break;
}}},
settings:{"qx.ioRemoteDebug":false,
"qx.ioRemoteDebugData":false},
destruct:function(){var bn=this.getImplementation();
if(bn){this.setImplementation(null);
bn.dispose();
}this.setRequest(null);
}});
})();
(function(){var a="qx.event.type.Event",
b="String",
c="failed",
d="timeout",
e="created",
f="aborted",
g="sending",
h="configured",
i="receiving",
j="completed",
k="Object",
l="Boolean",
m="abstract",
n="_applyState",
o="changeState",
p="qx.io.remote.transport.Abstract";
qx.Class.define(p,
{type:m,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
},
events:{"created":a,
"configured":a,
"sending":a,
"receiving":a,
"completed":a,
"aborted":a,
"failed":a,
"timeout":a},
properties:{url:{check:b,
nullable:true},
method:{check:b,
nullable:true},
asynchronous:{check:l,
nullable:true},
data:{check:b,
nullable:true},
username:{check:b,
nullable:true},
password:{check:b,
nullable:true},
state:{check:[e,
h,
g,
i,
j,
f,
d,
c],
init:e,
event:o,
apply:n},
requestHeaders:{check:k,
nullable:true},
parameters:{check:k,
nullable:true},
formFields:{check:k,
nullable:true},
responseType:{check:b,
nullable:true},
useBasicHttpAuth:{check:l,
nullable:true}},
members:{send:function(){throw new Error("send is abstract");
},
abort:function(){{};
this.setState(f);
},
timeout:function(){{};
this.setState(d);
},
failed:function(){{};
this.setState(c);
},
setRequestHeader:function(q,
r){throw new Error("setRequestHeader is abstract");
},
getResponseHeader:function(q){throw new Error("getResponseHeader is abstract");
},
getResponseHeaders:function(){throw new Error("getResponseHeaders is abstract");
},
getStatusCode:function(){throw new Error("getStatusCode is abstract");
},
getStatusText:function(){throw new Error("getStatusText is abstract");
},
getResponseText:function(){throw new Error("getResponseText is abstract");
},
getResponseXml:function(){throw new Error("getResponseXml is abstract");
},
getFetchedLength:function(){throw new Error("getFetchedLength is abstract");
},
_applyState:function(s,
t){{};
switch(s){case e:this.fireEvent(e);
break;
case h:this.fireEvent(h);
break;
case g:this.fireEvent(g);
break;
case i:this.fireEvent(i);
break;
case j:this.fireEvent(j);
break;
case f:this.fireEvent(f);
break;
case c:this.fireEvent(c);
break;
case d:this.fireEvent(d);
break;
}return true;
}}});
})();
(function(){var a="qx.event.type.Event",
b="completed",
c="failed",
d="aborted",
f="",
g="timeout",
h="application/xml",
j="qx.io.remote.transport.XmlHttp",
k="application/json",
m="text/html",
n="qx.client",
o="receiving",
p="text/plain",
q="text/javascript",
r="sending",
t="&",
u="configured",
v="?",
w="=",
x="created",
y='Referer',
z='Basic ',
A="\n</pre>",
B="string",
C='Authorization',
D="<pre>Could not execute json: \n",
E="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
F=':',
G="_req",
H="parseerror",
I="file:",
J="webkit",
K="object";
qx.Class.define(j,
{extend:qx.io.remote.transport.Abstract,
construct:function(){arguments.callee.base.call(this);
this._req=qx.io.remote.transport.XmlHttp.createRequestObject();
this._req.onreadystatechange=qx.lang.Function.bind(this._onreadystatechange,
this);
},
events:{"created":a,
"configured":a,
"sending":a,
"receiving":a,
"completed":a,
"aborted":a,
"failed":a,
"timeout":a},
statics:{handles:{synchronous:true,
asynchronous:true,
crossDomain:false,
fileUpload:false,
programaticFormFields:false,
responseTypes:[p,
q,
k,
h,
m]},
requestObjects:[],
requestObjectCount:0,
createRequestObject:qx.core.Variant.select(n,
{"default":function(){return new XMLHttpRequest;
},
"mshtml":function(){if(window.ActiveXObject&&qx.xml.Document.XMLHTTP){return new ActiveXObject(qx.xml.Document.XMLHTTP);
}
if(window.XMLHttpRequest){return new XMLHttpRequest;
}}}),
isSupported:function(){return !!this.createRequestObject();
},
__ij:function(){}},
members:{_localRequest:false,
_lastReadyState:0,
getRequest:function(){return this._req;
},
send:function(){this._lastReadyState=0;
var L=this.getRequest();
var M=this.getMethod();
var N=this.getAsynchronous();
var O=this.getUrl();
var P=(window.location.protocol===I&&!(/^http(s){0,1}\:/.test(O)));
this._localRequest=P;
var Q=this.getParameters();
var R=[];
for(var S in Q){var T=Q[S];
if(T instanceof Array){for(var U=0;U<T.length;U++){R.push(encodeURIComponent(S)+w+encodeURIComponent(T[U]));
}}else{R.push(encodeURIComponent(S)+w+encodeURIComponent(T));
}}
if(R.length>0){O+=(O.indexOf(v)>=0?t:v)+R.join(t);
}var V=function(W){var X=E;
var Y=f;
var ba,
bb,
bc;
var bd,
be,
bf,
bg;
var U=0;
do{ba=W.charCodeAt(U++);
bb=W.charCodeAt(U++);
bc=W.charCodeAt(U++);
bd=ba>>2;
be=((ba&3)<<4)|(bb>>4);
bf=((bb&15)<<2)|(bc>>6);
bg=bc&63;
if(isNaN(bb)){bf=bg=64;
}else if(isNaN(bc)){bg=64;
}Y+=X.charAt(bd)+X.charAt(be)+X.charAt(bf)+X.charAt(bg);
}while(U<W.length);
return Y;
};
L.onreadystatechange=qx.lang.Function.bind(this._onreadystatechange,
this);
try{if(this.getUsername()){if(this.getUseBasicHttpAuth()){L.open(M,
O,
N);
L.setRequestHeader(C,
z+V(this.getUsername()+F+this.getPassword()));
}else{L.open(M,
O,
N,
this.getUsername(),
this.getPassword());
}}else{L.open(M,
O,
N);
}}catch(ex){this.error("Failed with exception: "+ex);
this.failed();
return;
}if(!qx.core.Variant.isSet(n,
J)){L.setRequestHeader(y,
window.location.href);
}var bh=this.getRequestHeaders();
for(var S in bh){L.setRequestHeader(S,
bh[S]);
}try{{};
L.send(this.getData());
}catch(ex){if(P){this.failedLocally();
}else{this.error("Failed to send data: "+ex,
"send");
this.failed();
}return;
}if(!N){this._onreadystatechange();
}},
failedLocally:function(){if(this.getState()===c){return;
}this.warn("Could not load from file: "+this.getUrl());
this.failed();
},
_onreadystatechange:function(bi){switch(this.getState()){case b:case d:case c:case g:{};
return;
}var bj=this.getReadyState();
if(bj==4){if(!qx.io.remote.Exchange.wasSuccessful(this.getStatusCode(),
bj,
this._localRequest)){return this.failed();
}}while(this._lastReadyState<bj){this.setState(qx.io.remote.Exchange._nativeMap[++this._lastReadyState]);
}},
getReadyState:function(){var bj=null;
try{bj=this._req.readyState;
}catch(ex){}return bj;
},
setRequestHeader:function(bk,
bl){this._req.setRequestHeader(bk,
bl);
},
getResponseHeader:function(bk){var bm=null;
try{this.getRequest().getResponseHeader(bk)||null;
}catch(ex){}return bm;
},
getStringResponseHeaders:function(){var bn=null;
try{var bo=this._req.getAllResponseHeaders();
if(bo){bn=bo;
}}catch(ex){}return bn;
},
getResponseHeaders:function(){var bn=this.getStringResponseHeaders();
var bp={};
if(bn){var bq=bn.split(/[\r\n]+/g);
for(var U=0,
br=bq.length;U<br;U++){var bs=bq[U].match(/^([^:]+)\s*:\s*(.+)$/i);
if(bs){bp[bs[1]]=bs[2];
}}}return bp;
},
getStatusCode:function(){var bt=-1;
try{bt=this.getRequest().status;
}catch(ex){}return bt;
},
getStatusText:function(){var bu=f;
try{bu=this.getRequest().statusText;
}catch(ex){}return bu;
},
getResponseText:function(){var bv=null;
var bw=this.getStatusCode();
var bj=this.getReadyState();
if(qx.io.remote.Exchange.wasSuccessful(bw,
bj,
this._localRequest)){try{bv=this.getRequest().responseText;
}catch(ex){}}return bv;
},
getResponseXml:function(){var bx=null;
var bw=this.getStatusCode();
var bj=this.getReadyState();
if(qx.io.remote.Exchange.wasSuccessful(bw,
bj,
this._localRequest)){try{bx=this.getRequest().responseXML;
}catch(ex){}}if(typeof bx==K&&bx!=null){if(!bx.documentElement){var by=String(this.getRequest().responseText).replace(/<\?xml[^\?]*\?>/,
f);
bx.loadXML(by);
}if(!bx.documentElement){throw new Error("Missing Document Element!");
}
if(bx.documentElement.tagName==H){throw new Error("XML-File is not well-formed!");
}}else{throw new Error("Response was not a valid xml document ["+this.getRequest().responseText+"]");
}return bx;
},
getFetchedLength:function(){var bz=this.getResponseText();
return typeof bz==B?bz.length:0;
},
getResponseContent:function(){if(this.getState()!==b){{};
return null;
}{};
var bz=this.getResponseText();
switch(this.getResponseType()){case p:case m:{};
return bz;
case k:{};
try{if(bz&&bz.length>0){return qx.util.Json.parseQx(bz)||null;
}else{return null;
}}catch(ex){this.error("Could not execute json: ["+bz+"]",
ex);
return D+bz+A;
}case q:{};
try{if(bz&&bz.length>0){return window.eval(bz)||null;
}else{return null;
}}catch(ex){this.error("Could not execute javascript: ["+bz+"]",
ex);
return null;
}case h:bz=this.getResponseXml();
{};
return bz||null;
default:this.warn("No valid responseType specified ("+this.getResponseType()+")!");
return null;
}},
_applyState:function(T,
bA){{};
switch(T){case x:this.fireEvent(x);
break;
case u:this.fireEvent(u);
break;
case r:this.fireEvent(r);
break;
case o:this.fireEvent(o);
break;
case b:this.fireEvent(b);
break;
case c:this.fireEvent(c);
break;
case d:this.getRequest().abort();
this.fireEvent(d);
break;
case g:this.getRequest().abort();
this.fireEvent(g);
break;
}}},
defer:function(bB,
bC){qx.io.remote.Exchange.registerType(qx.io.remote.transport.XmlHttp,
j);
},
destruct:function(){var L=this.getRequest();
if(L){L.onreadystatechange=qx.io.remote.transport.XmlHttp.__ij;
switch(L.readyState){case 1:case 2:case 3:L.abort();
}}this._disposeFields(G);
}});
})();
(function(){var a="qx.client",
b="",
c="mshtml",
d='<?xml version="1.0" encoding="utf-8"?>\n<',
e="MSXML2.DOMDocument.3.0",
f="qx.xml.Document",
g=" />",
h="SelectionLanguage",
j="'",
k="MSXML2.XMLHTTP.3.0",
m="MSXML2.XMLHTTP.6.0",
n=" xmlns='",
o="text/xml",
p="XPath",
q="MSXML2.DOMDocument.6.0";
qx.Bootstrap.define(f,
{statics:{DOMDOC:null,
XMLHTTP:null,
create:qx.core.Variant.select(a,
{"mshtml":function(r,
s){var t=new ActiveXObject(this.DOMDOC);
t.setProperty(h,
p);
if(s){var u=d;
u+=s;
if(r){u+=n+r+j;
}u+=g;
t.loadXML(u);
}return t;
},
"default":function(r,
s){return document.implementation.createDocument(r||b,
s||b,
null);
}}),
fromString:qx.core.Variant.select(a,
{"mshtml":function(u){var v=qx.xml.Document.create();
v.loadXML(u);
return v;
},
"default":function(u){var w=new DOMParser();
return w.parseFromString(u,
o);
}})},
defer:function(x){if(qx.core.Variant.isSet(a,
c)){var y=[q,
e];
var z=[m,
k];
for(var A=0,
B=y.length;A<B;A++){try{new ActiveXObject(y[A]);
new ActiveXObject(z[A]);
}catch(ex){continue;
}x.DOMDOC=y[A];
x.XMLHTTP=z[A];
break;
}}}});
})();
(function(){var c=",",
d="",
e="string",
f="null",
g='"',
h="qx.jsonDebugging",
j='\\u00',
k="new Date(Date.UTC(",
m=")",
n='\\\\',
o='\\f',
p="__iw",
q="Object",
r='\\"',
s="))",
t="}",
u='(',
v=":",
w="{",
x='\\r',
y="__il",
z="(",
A='\\t',
B="__in",
C="]",
D="[",
E="__iv",
F="qx.jsonEncodeUndefined",
G='\\b',
H="qx.util.Json",
I=')',
J="__im",
K='\\n',
L="__io",
M="Date",
N="Array";
qx.Class.define(H,
{statics:{BEAUTIFYING_INDENT:"  ",
BEAUTIFYING_LINE_END:"\n",
__ik:{"function":y,
"boolean":J,
"number":B,
"string":L,
"object":E,
"undefined":p},
__il:function(O){return String(O);
},
__im:function(O){return String(O);
},
__in:function(O){return isFinite(O)?String(O):f;
},
__io:function(O){var P;
if(/["\\\x00-\x1f]/.test(O)){P=O.replace(/([\x00-\x1f\\"])/g,
qx.util.Json.__iq);
}else{P=O;
}return g+P+g;
},
__ip:{'\b':G,
'\t':A,
'\n':K,
'\f':o,
'\r':x,
'"':r,
'\\':n},
__iq:function(Q,
R){var P=qx.util.Json.__ip[R];
if(P){return P;
}P=R.charCodeAt();
return j+Math.floor(P/16).toString(16)+(P%16).toString(16);
},
__ir:function(O){var S=[],
T=true,
U,
V;
var W=qx.util.Json.__ix;
S.push(D);
if(W){qx.util.Json.__is+=qx.util.Json.BEAUTIFYING_INDENT;
S.push(qx.util.Json.__is);
}
for(var X=0,
Y=O.length;X<Y;X++){V=O[X];
U=this.__ik[typeof V];
if(U){V=this[U](V);
if(typeof V==e){if(!T){S.push(c);
if(W){S.push(qx.util.Json.__is);
}}S.push(V);
T=false;
}}}
if(W){qx.util.Json.__is=qx.util.Json.__is.substring(0,
qx.util.Json.__is.length-qx.util.Json.BEAUTIFYING_INDENT.length);
S.push(qx.util.Json.__is);
}S.push(C);
return S.join(d);
},
__it:function(O){var ba=O.getUTCFullYear()+c+O.getUTCMonth()+c+O.getUTCDate()+c+O.getUTCHours()+c+O.getUTCMinutes()+c+O.getUTCSeconds()+c+O.getUTCMilliseconds();
return k+ba+s;
},
__iu:function(O){var S=[],
T=true,
U,
V;
var W=qx.util.Json.__ix;
S.push(w);
if(W){qx.util.Json.__is+=qx.util.Json.BEAUTIFYING_INDENT;
S.push(qx.util.Json.__is);
}
for(var bb in O){V=O[bb];
U=this.__ik[typeof V];
if(U){V=this[U](V);
if(typeof V==e){if(!T){S.push(c);
if(W){S.push(qx.util.Json.__is);
}}S.push(this.__io(bb),
v,
V);
T=false;
}}}
if(W){qx.util.Json.__is=qx.util.Json.__is.substring(0,
qx.util.Json.__is.length-qx.util.Json.BEAUTIFYING_INDENT.length);
S.push(qx.util.Json.__is);
}S.push(t);
return S.join(d);
},
__iv:function(O){if(O){var bc=O.constructor.name;
if(O instanceof Array||bc==N){return this.__ir(O);
}else if(O instanceof Date||bc==M){return this.__it(O);
}else if(O instanceof Object||bc==q){return this.__iu(O);
}return d;
}return f;
},
__iw:function(O){if(qx.core.Setting.get(F)){return f;
}},
stringify:function(V,
W){this.__ix=W;
this.__is=this.BEAUTIFYING_LINE_END;
var P=this[this.__ik[typeof V]](V);
if(typeof P!=e){P=null;
}if(qx.core.Setting.get(h)){qx.log.Logger.debug(this,
"JSON request: "+P);
}return P;
},
parse:function(bd){if(/[^,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]/.test(bd.replace(/"(\\.|[^"\\])*"/g,
d))){throw new Error("Could not parse JSON string!");
}
try{return eval(z+bd+m);
}catch(ex){throw new Error("Could not evaluate JSON string: "+ex.message);
}},
parseQx:function(bd){if(qx.core.Setting.get(h)){qx.log.Logger.debug(this,
"JSON response: "+bd);
}var V=(bd&&bd.length>0)?eval(u+bd+I):null;
return V;
}},
settings:{"qx.jsonEncodeUndefined":true,
"qx.jsonDebugging":false}});
})();
(function(){var a="application/xml",
b="application/json",
c="text/html",
d="qx.client",
f="textarea",
g="none",
h="text/plain",
j="text/javascript",
k="",
l="completed",
m="?",
n="qx.io.remote.transport.Iframe",
o="&",
p="=",
q="gecko",
r="frame_",
s="__iy",
t="aborted",
u="_data_",
v="pre",
w="javascript:void(0)",
x="sending",
y="__iz",
z="form",
A="failed",
B='<iframe name="',
C="mshtml",
D="form_",
E='"></iframe>',
F="iframe",
G="timeout",
H="qx/static/blank.gif";
qx.Class.define(n,
{extend:qx.io.remote.transport.Abstract,
construct:function(){arguments.callee.base.call(this);
var I=(new Date).valueOf();
var J=r+I;
var K=D+I;
if(qx.core.Variant.isSet(d,
C)){this.__iy=document.createElement(B+J+E);
}else{this.__iy=document.createElement(F);
}this.__iy.src=w;
this.__iy.id=this.__iy.name=J;
this.__iy.onload=qx.lang.Function.bind(this._onload,
this);
this.__iy.style.display=g;
document.body.appendChild(this.__iy);
this.__iz=document.createElement(z);
this.__iz.target=J;
this.__iz.id=this.__iz.name=K;
this.__iz.style.display=g;
document.body.appendChild(this.__iz);
this._data=document.createElement(f);
this._data.id=this._data.name=u;
this.__iz.appendChild(this._data);
this.__iy.onreadystatechange=qx.lang.Function.bind(this._onreadystatechange,
this);
},
statics:{handles:{synchronous:false,
asynchronous:true,
crossDomain:false,
fileUpload:true,
programaticFormFields:true,
responseTypes:[h,
j,
b,
a,
c]},
isSupported:function(){return true;
},
_numericMap:{"uninitialized":1,
"loading":2,
"loaded":2,
"interactive":3,
"complete":4}},
members:{__iA:0,
__iz:null,
__iy:null,
send:function(){var L=this.getMethod();
var M=this.getUrl();
var N=this.getParameters();
var O=[];
for(var P in N){var Q=N[P];
if(Q instanceof Array){for(var R=0;R<Q.length;R++){O.push(encodeURIComponent(P)+p+encodeURIComponent(Q[R]));
}}else{O.push(encodeURIComponent(P)+p+encodeURIComponent(Q));
}}
if(O.length>0){M+=(M.indexOf(m)>=0?o:m)+O.join(o);
}var S=this.getFormFields();
for(var P in S){var T=document.createElement(f);
T.name=P;
T.appendChild(document.createTextNode(S[P]));
this.__iz.appendChild(T);
}this.__iz.action=M;
this.__iz.method=L;
this._data.appendChild(document.createTextNode(this.getData()));
this.__iz.submit();
this.setState(x);
},
_onload:function(U){if(this.__iz.src){return;
}this._switchReadyState(qx.io.remote.transport.Iframe._numericMap.complete);
},
_onreadystatechange:function(U){this._switchReadyState(qx.io.remote.transport.Iframe._numericMap[this.__iy.readyState]);
},
_switchReadyState:function(V){switch(this.getState()){case l:case t:case A:case G:this.warn("Ignore Ready State Change");
return;
}while(this.__iA<V){this.setState(qx.io.remote.Exchange._nativeMap[++this.__iA]);
}},
setRequestHeader:function(W,
X){},
getResponseHeader:function(W){return null;
},
getResponseHeaders:function(){return {};
},
getStatusCode:function(){return 200;
},
getStatusText:function(){return k;
},
getIframeWindow:function(){return qx.bom.Iframe.getWindow(this.__iy);
},
getIframeDocument:function(){return qx.bom.Iframe.getDocument(this.__iy);
},
getIframeBody:function(){return qx.bom.Iframe.getBody(this.__iy);
},
getIframeTextContent:function(){var Y=this.getIframeBody();
if(!Y){return null;
}
if(!Y.firstChild){return k;
}if(Y.firstChild.tagName&&Y.firstChild.tagName.toLowerCase()==v){return Y.firstChild.innerHTML;
}else{return Y.innerHTML;
}},
getIframeHtmlContent:function(){var Y=this.getIframeBody();
return Y?Y.innerHTML:null;
},
getFetchedLength:function(){return 0;
},
getResponseContent:function(){if(this.getState()!==l){{};
return null;
}{};
var ba=this.getIframeTextContent();
switch(this.getResponseType()){case h:{};
return ba;
break;
case c:ba=this.getIframeHtmlContent();
{};
return ba;
break;
case b:ba=this.getIframeHtmlContent();
{};
try{return ba&&ba.length>0?qx.util.Json.parseQx(ba):null;
}catch(ex){return this.error("Could not execute json: ("+ba+")",
ex);
}case j:ba=this.getIframeHtmlContent();
{};
try{return ba&&ba.length>0?window.eval(ba):null;
}catch(ex){return this.error("Could not execute javascript: ("+ba+")",
ex);
}case a:ba=this.getIframeDocument();
{};
return ba;
default:this.warn("No valid responseType specified ("+this.getResponseType()+")!");
return null;
}}},
defer:function(bb,
bc,
bd){qx.io.remote.Exchange.registerType(qx.io.remote.transport.Iframe,
n);
},
destruct:function(){if(this.__iy){this.__iy.onload=null;
this.__iy.onreadystatechange=null;
if(qx.core.Variant.isSet(d,
q)){this.__iy.src=qx.util.ResourceManager.toUri(H);
}document.body.removeChild(this.__iy);
}
if(this.__iz){document.body.removeChild(this.__iz);
}this._disposeFields(s,
y);
}});
})();
(function(){var a="qx.event.handler.Iframe",
b="load",
c="iframe";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{load:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:false,
onevent:function(d){qx.event.Registration.fireEvent(d,
b);
}},
members:{canHandleEvent:function(d,
e){return d.tagName.toLowerCase()===c;
},
registerEvent:function(d,
e,
f){},
unregisterEvent:function(d,
e,
f){}},
defer:function(g){qx.event.Registration.addHandler(g);
}});
})();
(function(){var a="0",
b="qx.client",
c="qx.bom.Iframe",
d="qx.event.handler.Iframe.onevent(this)",
e="true",
f="iframe",
g="body";
qx.Class.define(c,
{statics:{create:function(h,
i){var h=h?qx.lang.Object.copy(h):{};
h.onload=d;
h.frameBorder=a;
h.frameSpacing=a;
h.marginWidth=a;
h.marginHeight=a;
h.hspace=a;
h.vspace=a;
h.border=a;
h.allowTransparency=e;
return qx.bom.Element.create(f,
h,
i);
},
getWindow:qx.core.Variant.select(b,
{"mshtml|gecko":function(j){try{return j.contentWindow;
}catch(ex){return null;
}},
"default":function(j){try{var k=this.getDocument(j);
return k?k.defaultView:null;
}catch(ex){return null;
}}}),
getDocument:qx.core.Variant.select(b,
{"mshtml":function(j){try{var i=this.getWindow(j);
return i?i.document:null;
}catch(ex){return null;
}},
"default":function(j){try{return j.contentDocument;
}catch(ex){return null;
}}}),
getBody:function(j){var k=this.getDocument(j);
return k?k.getElementsByTagName(g)[0]:null;
},
setSource:function(j,
l){try{if(this.getWindow(j)){try{this.getWindow(j).location.replace(l);
}catch(ex){j.src=l;
}}else{j.src=l;
}}catch(ex){qx.log.Logger.warn("Iframe source could not be set!");
}},
queryCurrentUrl:function(j){var k=this.getDocument(j);
try{if(k&&k.location){return k.location.href;
}}catch(ex){}return null;
}}});
})();
(function(){var a="&",
b="=",
c="?",
d="application/json",
e="completed",
f="text/plain",
g="text/javascript",
h="qx.io.remote.transport.Script",
j="",
k="_ScriptTransport_data",
l="_responseContent",
m="script",
n="timeout",
o="_ScriptTransport_",
p="_element",
q="_ScriptTransport_id",
r="aborted",
s="utf-8",
t="failed";
qx.Class.define(h,
{extend:qx.io.remote.transport.Abstract,
construct:function(){arguments.callee.base.call(this);
var u=++qx.io.remote.transport.Script._uniqueId;
if(u>=2000000000){qx.io.remote.transport.Script._uniqueId=u=1;
}this._element=null;
this._uniqueId=u;
},
statics:{_uniqueId:0,
_instanceRegistry:{},
ScriptTransport_PREFIX:o,
ScriptTransport_ID_PARAM:q,
ScriptTransport_DATA_PARAM:k,
handles:{synchronous:false,
asynchronous:true,
crossDomain:true,
fileUpload:false,
programaticFormFields:false,
responseTypes:[f,
g,
d]},
isSupported:function(){return true;
},
_numericMap:{"uninitialized":1,
"loading":2,
"loaded":2,
"interactive":3,
"complete":4},
_requestFinished:function(v,
w){var x=qx.io.remote.transport.Script._instanceRegistry[v];
if(x==null){{};
}else{x._responseContent=w;
x._switchReadyState(qx.io.remote.transport.Script._numericMap.complete);
}}},
members:{_lastReadyState:0,
send:function(){var y=this.getUrl();
y+=(y.indexOf(c)>=0?a:c)+qx.io.remote.transport.Script.ScriptTransport_ID_PARAM+b+this._uniqueId;
var z=this.getParameters();
var A=[];
for(var B in z){if(B.indexOf(qx.io.remote.transport.Script.ScriptTransport_PREFIX)==0){this.error("Illegal parameter name. The following prefix is used internally by qooxdoo): "+qx.io.remote.transport.Script.ScriptTransport_PREFIX);
}var C=z[B];
if(C instanceof Array){for(var D=0;D<C.length;D++){A.push(encodeURIComponent(B)+b+encodeURIComponent(C[D]));
}}else{A.push(encodeURIComponent(B)+b+encodeURIComponent(C));
}}
if(A.length>0){y+=a+A.join(a);
}var E=this.getData();
if(E!=null){y+=a+qx.io.remote.transport.Script.ScriptTransport_DATA_PARAM+b+encodeURIComponent(E);
}qx.io.remote.transport.Script._instanceRegistry[this._uniqueId]=this;
this._element=document.createElement(m);
this._element.charset=s;
this._element.src=y;
{};
document.body.appendChild(this._element);
},
_switchReadyState:function(F){switch(this.getState()){case e:case r:case t:case n:this.warn("Ignore Ready State Change");
return;
}while(this._lastReadyState<F){this.setState(qx.io.remote.Exchange._nativeMap[++this._lastReadyState]);
}},
setRequestHeader:function(G,
H){},
getResponseHeader:function(G){return null;
},
getResponseHeaders:function(){return {};
},
getStatusCode:function(){return 200;
},
getStatusText:function(){return j;
},
getFetchedLength:function(){return 0;
},
getResponseContent:function(){if(this.getState()!==e){{};
return null;
}{};
switch(this.getResponseType()){case f:case d:case g:{};
return this._responseContent||null;
default:this.warn("No valid responseType specified ("+this.getResponseType()+")!");
return null;
}}},
defer:function(I,
J,
K){qx.io.remote.Exchange.registerType(qx.io.remote.transport.Script,
h);
qx.io.remote.ScriptTransport=I;
},
destruct:function(){if(this._element){delete qx.io.remote.transport.Script._instanceRegistry[this._uniqueId];
document.body.removeChild(this._element);
}this._disposeFields(p,
l);
}});
})();
(function(){var a="Integer",
b="Object",
c="qx.io.remote.Response";
qx.Class.define(c,
{extend:qx.event.type.Event,
properties:{state:{check:a,
nullable:true},
statusCode:{check:a,
nullable:true},
content:{nullable:true},
responseHeaders:{check:b,
nullable:true}},
members:{clone:function(d){var e=arguments.callee.base.call(this,
d);
e.setType(this.getType());
e.setState(this.getState());
e.setStatusCode(this.getStatusCode());
e.setContent(this.getContent());
e.setResponseHeaders(this.getResponseHeaders());
return e;
},
getResponseHeader:function(){var f=this.getResponseHeaders();
if(f){return f[vHeader]||null;
}return null;
}}});
})();
(function(){var a="slider",
b="splitter",
c="active",
d="horizontal",
f="vertical",
g="mousedown",
h="mouseout",
i="height",
j="row-resize",
k="mousemove",
l="move",
m="maxHeight",
n="width",
o="_applyOrientation",
p="splitpane",
q="qx.ui.splitpane.Pane",
r="minHeight",
s="knob",
t="mouseup",
u="minWidth",
v="losecapture",
w="col-resize",
x="maxWidth";
qx.Class.define(q,
{extend:qx.ui.core.Widget,
construct:function(y){arguments.callee.base.call(this);
if(y){this.setOrientation(y);
}else{this.initOrientation();
}this.addListener(g,
this._onMouseDown);
this.addListener(t,
this._onMouseUp);
this.addListener(k,
this._onMouseMove);
this.addListener(h,
this._onMouseOut);
this.addListener(v,
this._onMouseUp);
},
properties:{appearance:{refine:true,
init:p},
orientation:{init:d,
check:[d,
f],
apply:o}},
members:{__iB:null,
__iC:null,
__iD:null,
__iE:null,
__iF:null,
__iG:null,
__iH:null,
_createChildControlImpl:function(z){var A;
switch(z){case a:A=new qx.ui.splitpane.Slider(this);
A.exclude();
this._add(A,
{type:z});
break;
case b:A=new qx.ui.splitpane.Splitter(this);
this._add(A,
{type:z});
A.addListener(l,
this._onSplitterMove,
this);
break;
}return A||arguments.callee.base.call(this,
z);
},
_applyOrientation:function(B,
C){var D=this._getChildControl(a);
var E=this._getChildControl(b);
this.__iF=B===d;
var F=this._getLayout();
if(F){F.dispose();
}var G=B===f?new qx.ui.splitpane.VLayout:new qx.ui.splitpane.HLayout;
this._setLayout(G);
E.replaceState(C,
B);
E._getChildControl(s).replaceState(C,
B);
D.replaceState(C,
B);
},
add:function(H,
I){if(I==null){this._add(H);
}else{this._add(H,
{flex:I});
}},
remove:function(H){this._remove(H);
},
_onMouseDown:function(J){if(!J.isLeftPressed()){return;
}var E=this._getChildControl(b);
if(!E.hasState(c)){return;
}var K=E.getContainerLocation();
var L=this.getContentLocation();
this.__iB=this.__iF?J.getDocumentLeft()-K.left+L.left:J.getDocumentTop()-K.top+L.top;
var D=this._getChildControl(a);
var M=E.getBounds();
D.setUserBounds(M.left,
M.top,
M.width,
M.height);
D.setZIndex(E.getZIndex()+1);
D.show();
this.__iC=true;
this.capture();
},
_onMouseMove:function(J){this.__iD=J.getDocumentLeft();
this.__iE=J.getDocumentTop();
if(this.__iC){this.__iL();
var D=this._getChildControl(a);
var N=this.__iG;
if(this.__iF){D.setDomLeft(N);
}else{D.setDomTop(N);
}}else{this.__iK();
}},
_onMouseOut:function(J){this.__iD=-1;
this.__iE=-1;
this.__iK();
},
_onMouseUp:function(J){if(!this.__iC){return;
}this.__iI();
var D=this._getChildControl(a);
D.exclude();
delete this.__iC;
this.releaseCapture();
this.__iK();
},
_onSplitterMove:function(){this.__iK();
},
__iI:function(){var O=this.__iG;
var P=this.__iH;
if(O==null){return;
}var Q=this._getChildren();
var R=Q[2];
var S=Q[3];
var T=R.getLayoutProperties().flex;
var U=S.getLayoutProperties().flex;
if((T!=0)&&(U!=0)){R.setLayoutProperties({flex:O});
S.setLayoutProperties({flex:P});
}else{if(this.__iF){R.setWidth(O);
S.setWidth(P);
}else{R.setHeight(O);
S.setHeight(P);
}}},
__iJ:function(){var E=this._getChildControl(b);
var M=E.getBounds();
var K=E.getContainerLocation();
var V=6;
if(!K){return;
}var W=this.__iD;
var X=M.width;
var N=K.left;
if(X<V){N-=Math.floor((V-X)/2);
X=V;
}
if(W<N||W>(N+X)){return false;
}var W=this.__iE;
var X=M.height;
var N=K.top;
if(X<V){N-=Math.floor((V-X)/2);
X=V;
}
if(W<N||W>(N+X)){return false;
}return true;
},
__iK:function(){var E=this._getChildControl(b);
var Y=this.getApplicationRoot();
if(this.__iC||this.__iJ()){var ba=this.__iF?w:j;
this.setCursor(ba);
Y.setGlobalCursor(ba);
E.addState(c);
}else if(E.hasState(c)){this.resetCursor();
Y.resetGlobalCursor();
E.removeState(c);
}},
__iL:function(){if(this.__iF){var V=u,
X=n,
bb=x,
W=this.__iD;
}else{var V=r,
X=i,
bb=m,
W=this.__iE;
}var Q=this._getChildren();
var bc=Q[2].getSizeHint();
var bd=Q[3].getSizeHint();
var be=Q[2].getBounds()[X]+Q[3].getBounds()[X];
var O=W-this.__iB;
var P=be-O;
if(O<bc[V]){P-=bc[V]-O;
O=bc[V];
}else if(P<bd[V]){O-=bd[V]-P;
P=bd[V];
}if(O>bc[bb]){P+=O-bc[bb];
O=bc[bb];
}else if(P>bd[bb]){O+=P-bd[bb];
P=bd[bb];
}this.__iG=O;
this.__iH=P;
}}});
})();
(function(){var a="qx.ui.splitpane.Slider";
qx.Class.define(a,
{extend:qx.ui.core.Widget,
properties:{allowShrinkX:{refine:true,
init:false},
allowShrinkY:{refine:true,
init:false}}});
})();
(function(){var a="center",
b="knob",
c="middle",
d="qx.ui.splitpane.Splitter",
e="vertical";
qx.Class.define(d,
{extend:qx.ui.core.Widget,
construct:function(f){arguments.callee.base.call(this);
if(f.getOrientation()==e){this._setLayout(new qx.ui.layout.HBox(0,
a));
this._getLayout().setAlignY(c);
}else{this._setLayout(new qx.ui.layout.VBox(0,
c));
this._getLayout().setAlignX(a);
}this._createChildControl(b);
},
properties:{allowShrinkX:{refine:true,
init:false},
allowShrinkY:{refine:true,
init:false}},
members:{_createChildControlImpl:function(g){var h;
switch(g){case b:h=new qx.ui.basic.Image;
this._add(h);
break;
}return h||arguments.callee.base.call(this,
g);
}}});
})();
(function(){var a="slider",
b="splitter",
c="qx.ui.splitpane.VLayout";
qx.Class.define(c,
{extend:qx.ui.layout.Abstract,
members:{verifyLayoutProperty:null,
renderLayout:function(d,
e){var f=this._getLayoutChildren();
var g=f.length;
var h,
j;
var k,
l,
m,
n;
for(var o=0;o<g;o++){h=f[o];
j=h.getLayoutProperties().type;
if(j===b){l=h;
}else if(j===a){m=h;
}else if(!k){k=h;
}else{n=h;
}}
if(k&&n){var p=k.getLayoutProperties().flex;
var q=n.getLayoutProperties().flex;
if(p==null){p=1;
}
if(q==null){q=1;
}var r=k.getSizeHint();
var s=l.getSizeHint();
var t=n.getSizeHint();
var u=r.height;
var v=s.height;
var w=t.height;
if(p>0&&q>0){var x=p+q;
var y=e-v;
var u=Math.round((y/x)*p);
var w=y-u;
var z=qx.ui.layout.Util.arrangeIdeals(r.minHeight,
u,
r.maxHeight,
t.minHeight,
w,
t.maxHeight);
u=z.begin;
w=z.end;
}else if(p>0){u=e-v-w;
if(u<r.minHeight){u=r.minHeight;
}
if(u>r.maxHeight){u=r.maxHeight;
}}else if(q>0){w=e-u-v;
if(w<t.minHeight){w=t.minHeight;
}
if(w>t.maxHeight){w=t.maxHeight;
}}k.renderLayout(0,
0,
d,
u);
l.renderLayout(0,
u,
d,
v);
n.renderLayout(0,
u+v,
d,
w);
}else{l.renderLayout(0,
0,
0,
0);
if(k){k.renderLayout(0,
0,
d,
e);
}else if(n){n.renderLayout(0,
0,
d,
e);
}}},
_computeSizeHint:function(){var f=this._getLayoutChildren();
var g=f.length;
var h,
A,
B;
var C=0,
D=0,
E=0;
var F=0,
G=0,
H=0;
for(var o=0;o<g;o++){h=f[o];
B=h.getLayoutProperties();
if(B.type===a){continue;
}A=h.getSizeHint();
C+=A.minHeight;
D+=A.height;
E+=A.maxHeight;
if(A.minWidth>F){F=A.minWidth;
}
if(A.width>G){G=A.width;
}
if(A.maxWidth>H){H=A.maxWidth;
}}return {minHeight:C,
height:D,
maxHeight:E,
minWidth:F,
width:G,
maxWidth:H};
}}});
})();
(function(){var a="slider",
b="splitter",
c="qx.ui.splitpane.HLayout";
qx.Class.define(c,
{extend:qx.ui.layout.Abstract,
members:{verifyLayoutProperty:null,
renderLayout:function(d,
e){var f=this._getLayoutChildren();
var g=f.length;
var h,
j;
var k,
l,
m,
n;
for(var o=0;o<g;o++){h=f[o];
j=h.getLayoutProperties().type;
if(j===b){l=h;
}else if(j===a){m=h;
}else if(!k){k=h;
}else{n=h;
}}
if(k&&n){var p=k.getLayoutProperties().flex;
var q=n.getLayoutProperties().flex;
if(p==null){p=1;
}
if(q==null){q=1;
}var r=k.getSizeHint();
var s=l.getSizeHint();
var t=n.getSizeHint();
var u=r.width;
var v=s.width;
var w=t.width;
if(p>0&&q>0){var x=p+q;
var y=d-v;
var u=Math.round((y/x)*p);
var w=y-u;
var z=qx.ui.layout.Util.arrangeIdeals(r.minWidth,
u,
r.maxWidth,
t.minWidth,
w,
t.maxWidth);
u=z.begin;
w=z.end;
}else if(p>0){u=d-v-w;
if(u<r.minWidth){u=r.minWidth;
}
if(u>r.maxWidth){u=r.maxWidth;
}}else if(q>0){w=d-u-v;
if(w<t.minWidth){w=t.minWidth;
}
if(w>t.maxWidth){w=t.maxWidth;
}}k.renderLayout(0,
0,
u,
e);
l.renderLayout(u,
0,
v,
e);
n.renderLayout(u+v,
0,
w,
e);
}else{l.renderLayout(0,
0,
0,
0);
if(k){k.renderLayout(0,
0,
d,
e);
}else if(n){n.renderLayout(0,
0,
d,
e);
}}},
_computeSizeHint:function(){var f=this._getLayoutChildren();
var g=f.length;
var h,
A,
B;
var C=0,
D=0,
E=0;
var F=0,
G=0,
H=0;
for(var o=0;o<g;o++){h=f[o];
B=h.getLayoutProperties();
if(B.type===a){continue;
}A=h.getSizeHint();
C+=A.minWidth;
D+=A.width;
E+=A.maxWidth;
if(A.minHeight>F){F=A.minHeight;
}
if(A.height>G){G=A.height;
}
if(A.maxHeight>H){H=A.maxHeight;
}}return {minWidth:C,
width:D,
maxWidth:E,
minHeight:F,
height:G,
maxHeight:H};
}}});
})();
(function(){var a="",
b="solid",
c="app-header",
d="org.argeo.slc.web.components.View",
e="#000";
qx.Class.define(d,
{extend:qx.ui.container.Composite,
construct:function(f,
g){arguments.callee.base.call(this);
this.setViewId(f);
this.setViewTitle(g);
var h=new org.argeo.slc.web.components.ViewSelection(f);
this.setViewSelection(h);
this.createGui();
},
properties:{viewId:{init:a},
viewTitle:{init:a},
viewSelection:{nullable:false},
ownScrollable:{init:false}},
members:{createGui:function(){this.setLayout(new qx.ui.layout.VBox());
this.header=new qx.ui.container.Composite();
this.header.setLayout(new qx.ui.layout.HBox());
this.header.set({appearance:c});
this.header.add(new qx.ui.basic.Label(this.getViewTitle()));
this.add(this.header);
this.setDecorator(new qx.ui.decoration.Single(1,
b,
e));
},
setContent:function(i,
j){if(j){this.setOwnScrollable(true);
this.scrollable=new qx.ui.container.Scroll(i);
this.add(this.scrollable,
{flex:1});
}else{this.content=i;
this.add(this.content,
{flex:1});
}},
empty:function(){if(this.getOwnScrollable()&&this.scrollable){this.remove(this.scrollable);
}else if(this.content){this.remove(this.content);
}}}});
})();
(function(){var a="changeSelection",
b="org.argeo.slc.web.components.ViewSelection",
c="qx.event.type.Data";
qx.Class.define(b,
{extend:qx.core.Object,
construct:function(d){arguments.callee.base.call(this);
this.nodes=[];
this.setViewId(d);
},
properties:{viewId:{check:String,
nullable:false}},
events:{"changeSelection":c},
members:{clear:function(){this.nodes=[];
this.triggerEvent();
},
addNode:function(e){this.nodes.push(e);
this.triggerEvent();
},
getCount:function(){return this.nodes.length;
},
getNodes:function(){return this.nodes;
},
triggerEvent:function(){this.fireDataEvent(a,
this);
}}});
})();
(function(){var a="_applyStyle",
b="solid",
c="Color",
d="double",
e="px ",
f="dotted",
g="_applyWidth",
h="dashed",
i="Number",
j=" ",
k=";",
l="shorthand",
m="repeat",
n="px",
o="widthTop",
p="scale",
q="styleRight",
r="styleBottom",
s="widthLeft",
t="widthBottom",
u="",
v="styleTop",
w="colorBottom",
x="styleLeft",
y="widthRight",
z="colorLeft",
A="colorRight",
B="colorTop",
C="border-left:",
D="position:absolute;top:0;left:0;",
E="__iN",
F="repeat-y",
G="String",
H="border-bottom:",
I="border-right:",
J="qx.ui.decoration.Single",
K="border-top:",
L="__iM",
M="no-repeat",
N="repeat-x";
qx.Class.define(J,
{extend:qx.core.Object,
implement:[qx.ui.decoration.IDecorator],
construct:function(O,
P,
Q){arguments.callee.base.call(this);
if(O!=null){this.setWidth(O);
}
if(P!=null){this.setStyle(P);
}
if(Q!=null){this.setColor(Q);
}},
properties:{widthTop:{check:i,
init:0,
apply:g},
widthRight:{check:i,
init:0,
apply:g},
widthBottom:{check:i,
init:0,
apply:g},
widthLeft:{check:i,
init:0,
apply:g},
styleTop:{nullable:true,
check:[b,
f,
h,
d],
init:b,
apply:a},
styleRight:{nullable:true,
check:[b,
f,
h,
d],
init:b,
apply:a},
styleBottom:{nullable:true,
check:[b,
f,
h,
d],
init:b,
apply:a},
styleLeft:{nullable:true,
check:[b,
f,
h,
d],
init:b,
apply:a},
colorTop:{nullable:true,
check:c,
apply:a},
colorRight:{nullable:true,
check:c,
apply:a},
colorBottom:{nullable:true,
check:c,
apply:a},
colorLeft:{nullable:true,
check:c,
apply:a},
backgroundImage:{check:G,
nullable:true,
apply:a},
backgroundRepeat:{check:[m,
N,
F,
M,
p],
init:m,
apply:a},
backgroundColor:{check:c,
nullable:true,
apply:a},
left:{group:[s,
x,
z]},
right:{group:[y,
q,
A]},
top:{group:[o,
v,
B]},
bottom:{group:[t,
r,
w]},
width:{group:[o,
y,
t,
s],
mode:l},
style:{group:[v,
q,
r,
x],
mode:l},
color:{group:[B,
A,
w,
z],
mode:l}},
members:{init:function(R){R.useMarkup(this.getMarkup());
},
getMarkup:function(R){if(this.__iM){return this.__iM;
}var S=qx.theme.manager.Color.getInstance();
var T=u;
var O=this.getWidthTop();
if(O>0){T+=K+O+e+this.getStyleTop()+j+S.resolve(this.getColorTop())+k;
}var O=this.getWidthRight();
if(O>0){T+=I+O+e+this.getStyleRight()+j+S.resolve(this.getColorRight())+k;
}var O=this.getWidthBottom();
if(O>0){T+=H+O+e+this.getStyleBottom()+j+S.resolve(this.getColorBottom())+k;
}var O=this.getWidthLeft();
if(O>0){T+=C+O+e+this.getStyleLeft()+j+S.resolve(this.getColorLeft())+k;
}{};
T+=D;
var U=qx.ui.decoration.Util.generateBackgroundMarkup(this.getBackgroundImage(),
this.getBackgroundRepeat(),
T);
return this.__iM=U;
},
resize:function(R,
O,
V){var W=this.getBackgroundImage()&&this.getBackgroundRepeat()==p;
if(W||qx.bom.client.Feature.CONTENT_BOX){var X=this.getInsets();
O-=X.left+X.right;
V-=X.top+X.bottom;
if(O<0){O=0;
}
if(V<0){V=0;
}}var Y=R.getDomElement();
Y.style.width=O+n;
Y.style.height=V+n;
},
tint:function(R,
ba){var S=qx.theme.manager.Color.getInstance();
var Y=R.getDomElement();
if(ba==null){ba=this.getBackgroundColor();
}Y.style.backgroundColor=S.resolve(ba)||u;
},
getInsets:function(){if(this.__iN){return this.__iN;
}this.__iN={top:this.getWidthTop(),
right:this.getWidthRight(),
bottom:this.getWidthBottom(),
left:this.getWidthLeft()};
return this.__iN;
},
_applyWidth:function(){{};
this.__iN=null;
},
_applyStyle:function(){{};
}},
destruct:function(){this._disposeFields(L,
E);
}});
})();
(function(){var a="qx.bom.client.Feature";
qx.Bootstrap.define(a,
{statics:{STANDARD_MODE:false,
QUIRKS_MODE:false,
CONTENT_BOX:false,
BORDER_BOX:false,
SVG:false,
CANVAS:false,
VML:false,
XPATH:false,
__iO:function(){this.STANDARD_MODE=document.compatMode==="CSS1Compat";
this.QUIRKS_MODE=!this.STANDARD_MODE;
this.CONTENT_BOX=!qx.bom.client.Engine.MSHTML||this.STANDARD_MODE;
this.BORDER_BOX=!this.CONTENT_BOX;
this.SVG=document.implementation&&document.implementation.hasFeature&&document.implementation.hasFeature("org.w3c.dom.svg",
"1.0");
this.CANVAS=!!window.CanvasRenderingContext2D;
this.VML=qx.bom.client.Engine.MSHTML;
this.AIR=navigator.userAgent.indexOf("adobeair")!==-1;
this.GEARS=!!(window.google&&window.google.gears);
this.XPATH=!!document.evaluate;
}},
defer:function(b){b.__iO();
}});
})();
(function(){var a="scrollbar-y",
b="pane",
c="scrollbar-x",
d="auto",
f="corner",
g="on",
h="changeVisibility",
i="scroll",
j="_computeScrollbars",
k="off",
l="scrollY",
m="abstract",
n="update",
o="scrollX",
p="mousewheel",
q="scrollbarY",
r="scrollbarX",
s="horizontal",
t="scrollarea",
u="qx.ui.core.AbstractScrollArea",
v="vertical";
qx.Class.define(u,
{extend:qx.ui.core.Widget,
type:m,
construct:function(){arguments.callee.base.call(this);
var w=new qx.ui.layout.Grid();
w.setColumnFlex(0,
1);
w.setRowFlex(0,
1);
this._setLayout(w);
this.addListener(p,
this._onMouseWheel,
this);
},
properties:{appearance:{refine:true,
init:t},
width:{refine:true,
init:100},
height:{refine:true,
init:200},
scrollbarX:{check:[d,
g,
k],
init:d,
apply:j},
scrollbarY:{check:[d,
g,
k],
init:d,
apply:j},
scrollbar:{group:[r,
q]}},
members:{_createChildControlImpl:function(x){var y;
switch(x){case b:y=new qx.ui.core.ScrollPane();
y.addListener(n,
this._computeScrollbars,
this);
y.addListener(o,
this._onScrollPaneX,
this);
y.addListener(l,
this._onScrollPaneY,
this);
this._add(y,
{row:0,
column:0});
break;
case c:y=new qx.ui.core.ScrollBar(s);
y.exclude();
y.addListener(i,
this._onScrollBarX,
this);
y.addListener(h,
this._onChangeScrollbarXVisibility,
this);
this._add(y,
{row:1,
column:0});
break;
case a:y=new qx.ui.core.ScrollBar(v);
y.exclude();
y.addListener(i,
this._onScrollBarY,
this);
y.addListener(h,
this._onChangeScrollbarYVisibility,
this);
this._add(y,
{row:0,
column:1});
break;
case f:y=new qx.ui.core.Widget();
y.setWidth(0);
y.setHeight(0);
y.exclude();
this._add(y,
{row:1,
column:1});
break;
}return y||arguments.callee.base.call(this,
x);
},
getPaneSize:function(){return this._getChildControl(b).getBounds();
},
getItemTop:function(z){return this._getChildControl(b).getItemTop(z);
},
getItemBottom:function(z){return this._getChildControl(b).getItemBottom(z);
},
getItemLeft:function(z){return this._getChildControl(b).getItemLeft(z);
},
getItemRight:function(z){return this._getChildControl(b).getItemRight(z);
},
scrollToX:function(A){this._getChildControl(c).scrollTo(A);
},
scrollByX:function(A){this._getChildControl(c).scrollBy(A);
},
getScrollX:function(){var B=this._getChildControl(c,
true);
return B?B.getPosition():0;
},
scrollToY:function(A){this._getChildControl(a).scrollTo(A);
},
scrollByY:function(A){this._getChildControl(a).scrollBy(A);
},
getScrollY:function(){var B=this._getChildControl(a,
true);
return B?B.getPosition():0;
},
_onScrollBarX:function(C){this._getChildControl(b).scrollToX(C.getData());
},
_onScrollBarY:function(C){this._getChildControl(b).scrollToY(C.getData());
},
_onScrollPaneX:function(C){this.scrollToX(C.getData());
},
_onScrollPaneY:function(C){this.scrollToY(C.getData());
},
_onMouseWheel:function(C){var B=this._getChildControl(a,
true);
if(B){B.scrollBySteps(C.getWheelDelta());
}C.stop();
},
_onChangeScrollbarXVisibility:function(C){var D=this._isChildControlVisible(c);
var E=this._isChildControlVisible(a);
if(!D){this.scrollToX(0);
}D&&E?this._showChildControl(f):this._excludeChildControl(f);
},
_onChangeScrollbarYVisibility:function(C){var D=this._isChildControlVisible(c);
var E=this._isChildControlVisible(a);
if(!E){this.scrollToY(0);
}D&&E?this._showChildControl(f):this._excludeChildControl(f);
},
_computeScrollbars:function(){var F=this._getChildControl(b);
var G=F.getChild();
if(!G){this._excludeChildControl(c);
this._excludeChildControl(a);
return;
}var H=this.getInnerSize();
var I=F.getBounds();
var J=F.getScrollSize();
if(!I||!J){return;
}var K=this.getScrollbarX();
var L=this.getScrollbarY();
if(K===d&&L===d){var D=J.width>H.width;
var E=J.height>H.height;
if((D||E)&&!(D&&E)){if(D){E=J.height>I.height;
}else if(E){D=J.width>I.width;
}}}else{var D=K===g;
var E=L===g;
if(J.width>(D?I.width:H.width)&&K===d){D=true;
}
if(J.height>(D?I.height:H.height)&&L===d){E=true;
}}if(D){var M=this._getChildControl(c);
M.show();
M.setMaximum(Math.max(0,
J.width-I.width));
M.setKnobFactor(I.width/J.width);
}else{this._excludeChildControl(c);
}
if(E){var N=this._getChildControl(a);
N.show();
N.setMaximum(Math.max(0,
J.height-I.height));
N.setKnobFactor(I.height/J.height);
}else{this._excludeChildControl(a);
}}}});
})();
(function(){var a="pane",
b="qx.ui.container.Scroll";
qx.Class.define(b,
{extend:qx.ui.core.AbstractScrollArea,
include:[qx.ui.core.MContentPadding],
construct:function(c){arguments.callee.base.call(this);
if(c){this.add(c);
}},
members:{add:function(d){this._getChildControl(a).add(d);
},
remove:function(d){this._getChildControl(a).remove(d);
},
getChild:function(){return this._getChildControl(a).getChild();
},
_getContentPaddingTarget:function(){return this._getChildControl(a);
}}});
})();
(function(){var a="resize",
b="scrollY",
c="typeof value=='number'&&value>=0&&value<=this.getScrollMaxX()",
d="update",
f="scrollX",
g="_applyScrollX",
h="_applyScrollY",
i="appear",
j="qx.ui.core.ScrollPane",
k="qx.event.type.Event",
l="typeof value=='number'&&value>=0&&value<=this.getScrollMaxY()",
m="scroll";
qx.Class.define(j,
{extend:qx.ui.core.Widget,
construct:function(){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.Grow());
this.addListener(a,
this._onUpdate);
var n=this.getContentElement();
n.addListener(m,
this._onScroll,
this);
n.addListener(i,
this._onAppear,
this);
},
events:{update:k},
properties:{scrollX:{check:c,
apply:g,
event:f,
init:0},
scrollY:{check:l,
apply:h,
event:b,
init:0}},
members:{add:function(o){var p=this._getChildren()[0];
if(p){this._remove(p);
p.removeListener(a,
this._onUpdate,
this);
}
if(o){this._add(o);
o.addListener(a,
this._onUpdate,
this);
}},
remove:function(o){if(o){this._remove(o);
o.removeListener(a,
this._onUpdate,
this);
}},
getChild:function(){return this._getChildren()[0]||null;
},
_onUpdate:function(q){this.fireEvent(d);
},
_onScroll:function(q){var n=this.getContentElement();
this.setScrollX(n.getScrollX());
this.setScrollY(n.getScrollY());
},
_onAppear:function(q){var n=this.getContentElement();
var r=this.getScrollX();
var s=n.getScrollX();
if(r!=s){n.scrollToX(r);
}var t=this.getScrollY();
var u=n.getScrollY();
if(t!=u){n.scrollToY(t);
}},
getItemTop:function(v){var w=0;
do{w+=v.getBounds().top;
v=v.getLayoutParent();
}while(v&&v!==this);
return w;
},
getItemBottom:function(v){return this.getItemTop(v)+v.getBounds().height;
},
getItemLeft:function(v){var z=0;
var A;
do{z+=v.getBounds().left;
A=v.getLayoutParent();
if(A){z+=A.getInsets().left;
}v=A;
}while(v&&v!==this);
return z;
},
getItemRight:function(v){return this.getItemLeft(v)+v.getBounds().width;
},
getScrollSize:function(){return this.getChild().getBounds();
},
getScrollMaxX:function(){var B=this.getBounds();
var C=this.getScrollSize();
if(B&&C){return Math.max(0,
C.width-B.width);
}return 0;
},
getScrollMaxY:function(){var B=this.getBounds();
var C=this.getScrollSize();
if(B&&C){return Math.max(0,
C.height-B.height);
}return 0;
},
scrollToX:function(D){var E=this.getScrollMaxX();
if(D<0){D=0;
}else if(D>E){D=E;
}this.setScrollX(D);
},
scrollToY:function(D){var E=this.getScrollMaxY();
if(D<0){D=0;
}else if(D>E){D=E;
}this.setScrollY(D);
},
scrollByX:function(F){this.scrollToX(this.getScrollX()+F);
},
scrollByY:function(G){this.scrollToY(this.getScrollY()+G);
},
_applyScrollX:function(D){this.getContentElement().scrollToX(D);
},
_applyScrollY:function(D){this.getContentElement().scrollToY(D);
}}});
})();
(function(){var a="slider",
b="horizontal",
c="button-begin",
d="button-end",
f="vertical",
g="Integer",
h="execute",
i="right",
j="left",
k="down",
l="up",
m="PositiveNumber",
n="changeValue",
o="typeof value==='number'&&value>=0&&value<=this.getMaximum()",
p="_applyKnobFactor",
q="_applyOrientation",
r="qx.ui.core.ScrollBar",
s="_applyPageStep",
t="PositiveInteger",
u="scroll",
v="_applyPosition",
w="scrollbar",
x="_applyMaximum";
qx.Class.define(r,
{extend:qx.ui.core.Widget,
construct:function(y){arguments.callee.base.call(this);
this._createChildControl(c);
this._createChildControl(a);
this._createChildControl(d);
if(y!=null){this.setOrientation(y);
}else{this.initOrientation();
}},
properties:{appearance:{refine:true,
init:w},
orientation:{check:[b,
f],
init:b,
apply:q},
maximum:{check:t,
apply:x,
init:100},
position:{check:o,
init:0,
apply:v,
event:u},
singleStep:{check:g,
init:20},
pageStep:{check:g,
init:10,
apply:s},
knobFactor:{check:m,
apply:p,
nullable:true}},
members:{_createChildControlImpl:function(z){var A;
switch(z){case a:A=new qx.ui.core.ScrollSlider;
A.setPageStep(100);
A.setFocusable(false);
A.addListener(n,
this._onChangeSliderValue,
this);
this._add(A,
{flex:1});
break;
case c:A=new qx.ui.form.RepeatButton;
A.setFocusable(false);
A.addListener(h,
this._onExecuteBegin,
this);
this._add(A);
break;
case d:A=new qx.ui.form.RepeatButton;
A.setFocusable(false);
A.addListener(h,
this._onExecuteEnd,
this);
this._add(A);
break;
}return A||arguments.callee.base.call(this,
z);
},
_applyMaximum:function(B){this._getChildControl(a).setMaximum(B);
},
_applyPosition:function(B){this._getChildControl(a).setValue(B);
},
_applyKnobFactor:function(B){this._getChildControl(a).setKnobFactor(B);
},
_applyPageStep:function(B){this._getChildControl(a).setPageStep(B);
},
_applyOrientation:function(B,
C){var D=this._getLayout();
if(D){D.dispose();
}if(B===b){this._setLayout(new qx.ui.layout.HBox());
this.setAllowStretchX(true);
this.setAllowStretchY(false);
this.replaceState(f,
b);
this._getChildControl(c).replaceState(l,
j);
this._getChildControl(d).replaceState(k,
i);
}else{this._setLayout(new qx.ui.layout.VBox());
this.setAllowStretchX(false);
this.setAllowStretchY(true);
this.replaceState(b,
f);
this._getChildControl(c).replaceState(j,
l);
this._getChildControl(d).replaceState(i,
k);
}this._getChildControl(a).setOrientation(B);
},
scrollTo:function(E){this._getChildControl(a).slideTo(E);
},
scrollBy:function(F){this._getChildControl(a).slideBy(F);
},
scrollBySteps:function(G){var H=this.getSingleStep();
this._getChildControl(a).slideBy(G*H);
},
_onExecuteBegin:function(I){this.scrollBy(-this.getSingleStep());
},
_onExecuteEnd:function(I){this.scrollBy(this.getSingleStep());
},
_onChangeSliderValue:function(I){this.setPosition(I.getData());
}}});
})();
(function(){var a="knob",
b="horizontal",
c="vertical",
d="Integer",
f="px",
g="mousemove",
h="resize",
i="left",
j="top",
k="mouseup",
l="slider",
m="PageUp",
n="mousedown",
o="height",
p="changeValue",
q="Left",
r="Down",
s="Up",
t="dblclick",
u="qx.ui.form.Slider",
v="PageDown",
w="mousewheel",
x="interval",
y="_applyValue",
z="_applyKnobFactor",
A="End",
B="String",
C="width",
D="_applyOrientation",
E="Home",
F="floor",
G="_applyMinimum",
H="click",
I="Right",
J="keypress",
K="ceil",
L="changeName",
M="losecapture",
N="contextmenu",
O="_applyMaximum",
P="Number",
Q="typeof value==='number'&&value>=this.getMinimum()&&value<=this.getMaximum()";
qx.Class.define(u,
{extend:qx.ui.core.Widget,
implement:qx.ui.form.IFormElement,
construct:function(R){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.Canvas());
this.addListener(J,
this._onKeyPress);
this.addListener(w,
this._onMouseWheel);
this.addListener(n,
this._onMouseDown);
this.addListener(k,
this._onMouseUp);
this.addListener(M,
this._onMouseUp);
this.addListener(h,
this._onUpdate);
this.addListener(N,
this._onStopEvent);
this.addListener(H,
this._onStopEvent);
this.addListener(t,
this._onStopEvent);
if(R!=null){this.setOrientation(R);
}else{this.initOrientation();
}},
properties:{appearance:{refine:true,
init:l},
focusable:{refine:true,
init:true},
orientation:{check:[b,
c],
init:b,
apply:D},
name:{check:B,
nullable:true,
event:L},
value:{check:Q,
init:0,
apply:y,
event:p},
minimum:{check:d,
init:0,
apply:G},
maximum:{check:d,
init:100,
apply:O},
singleStep:{check:d,
init:1},
pageStep:{check:d,
init:10},
knobFactor:{check:P,
apply:z,
nullable:true}},
members:{__iP:null,
__iQ:null,
__iR:null,
__iS:null,
__iT:null,
__iU:null,
__iV:null,
__iW:null,
__iX:null,
_createChildControlImpl:function(S){var T;
switch(S){case a:T=new qx.ui.core.Widget();
T.addListener(h,
this._onUpdate,
this);
this._add(T);
break;
}return T||arguments.callee.base.call(this,
S);
},
_onMouseWheel:function(U){var V=U.getWheelDelta()>0?1:-1;
this.slideBy(V*this.getSingleStep());
U.stop();
},
_onKeyPress:function(U){var W=this.getOrientation()===b;
var X=W?q:s;
var Y=W?I:r;
switch(U.getKeyIdentifier()){case Y:this.slideForward();
break;
case X:this.slideBack();
break;
case v:this.slidePageForward();
break;
case m:this.slidePageBack();
break;
case E:this.slideToBegin();
break;
case A:this.slideToEnd();
break;
default:return;
}U.stop();
},
_onMouseDown:function(U){var W=this.__ja;
var ba=this._getChildControl(a);
var bb=W?i:j;
var bc=W?U.getDocumentLeft():U.getDocumentTop();
var bd=this.__iP=qx.bom.element.Location.get(this.getContentElement().getDomElement())[bb];
var be=this.__iQ=qx.bom.element.Location.get(ba.getContainerElement().getDomElement())[bb];
if(U.getTarget()===ba){this.__iS=true;
this.__iT=bc+bd-be;
}else{this.__iU=true;
this.__iV=bc<=be?-1:1;
this.__jb(U);
this._onInterval();
if(!this.__iX){this.__iX=new qx.event.Timer(100);
this.__iX.addListener(x,
this._onInterval,
this);
}this.__iX.start();
}this.addListener(g,
this._onMouseMove);
this.capture();
U.stopPropagation();
},
_onMouseUp:function(U){if(this.__iS){this.releaseCapture();
delete this.__iS;
delete this.__iT;
}else if(this.__iU){this.__iX.stop();
this.releaseCapture();
delete this.__iU;
delete this.__iV;
delete this.__iW;
}this.removeListener(g,
this._onMouseMove);
if(U.getType()===k){U.stopPropagation();
}},
_onMouseMove:function(U){if(this.__iS){var bf=this.__ja?U.getDocumentLeft():U.getDocumentTop();
var bg=bf-this.__iT;
this.slideTo(this._positionToValue(bg));
}else if(this.__iU){this.__jb(U);
}U.stopPropagation();
},
_onInterval:function(U){var bh=this.getValue()+(this.__iV*this.getPageStep());
if(bh<this.getMinimum()){bh=this.getMinimum();
}else if(bh>this.getMaximum()){bh=this.getMaximum();
}var bi=this.__iV==-1;
if((bi&&bh<=this.__iW)||(!bi&&bh>=this.__iW)){bh=this.__iW;
}this.slideTo(bh);
},
_onUpdate:function(U){var bj=this.getInnerSize();
var bk=this._getChildControl(a).getBounds();
var bl=this.__ja?C:o;
this._updateKnobSize();
this.__iY=bj[bl]-bk[bl];
this.__iR=bk[bl];
this._updateKnobPosition();
},
__ja:false,
__iY:0,
__jb:function(U){var W=this.__ja;
var bc=W?U.getDocumentLeft():U.getDocumentTop();
var bd=this.__iP;
var be=this.__iQ;
var bk=this.__iR;
var bg=bc-bd;
if(bc>=be){bg-=bk;
}var bh=this._positionToValue(bg);
var bm=this.getMinimum();
var bn=this.getMaximum();
if(bh<bm){bh=bm;
}else if(bh>bn){bh=bn;
}else{var bo=this.getValue();
var bp=this.getPageStep();
var bq=this.__iV<0?F:K;
bh=bo+(Math[bq]((bh-bo)/bp)*bp);
}if(this.__iW==null||(this.__iV==-1&&bh<=this.__iW)||(this.__iV==1&&bh>=this.__iW)){this.__iW=bh;
}},
_positionToValue:function(bg){var br=this.__iY;
if(br==null||br==0){return 0;
}var bs=bg/br;
if(bs<0){bs=0;
}else if(bs>1){bs=1;
}var bt=this.getMaximum()-this.getMinimum();
return this.getMinimum()+Math.round(bt*bs);
},
_valueToPosition:function(bh){var br=this.__iY;
if(br==null){return 0;
}var bt=this.getMaximum()-this.getMinimum();
if(bt==0){return 0;
}var bh=bh-this.getMinimum();
var bs=bh/bt;
if(bs<0){bs=0;
}else if(bs>1){bs=1;
}return Math.round(br*bs);
},
_updateKnobPosition:function(){this._setKnobPosition(this._valueToPosition(this.getValue()));
},
_setKnobPosition:function(bg){var bu=this._getChildControl(a).getContainerElement();
if(this.__ja){bu.setStyle(i,
bg+f,
true);
}else{bu.setStyle(j,
bg+f,
true);
}},
_updateKnobSize:function(){var bv=this.getKnobFactor();
if(bv==null){return;
}var br=this.getInnerSize();
if(br==null){return;
}if(this.__ja){this._getChildControl(a).setWidth(Math.round(bv*br.width));
}else{this._getChildControl(a).setHeight(Math.round(bv*br.height));
}},
slideToBegin:function(){this.slideTo(this.getMinimum());
},
slideToEnd:function(){this.slideTo(this.getMaximum());
},
slideForward:function(){this.slideBy(this.getSingleStep());
},
slideBack:function(){this.slideBy(-this.getSingleStep());
},
slidePageForward:function(){this.slideBy(this.getPageStep());
},
slidePageBack:function(){this.slideBy(-this.getPageStep());
},
slideBy:function(bw){this.slideTo(this.getValue()+bw);
},
slideTo:function(bh){if(bh<this.getMinimum()){bh=this.getMinimum();
}else if(bh>this.getMaximum()){bh=this.getMaximum();
}else{bh=this.getMinimum()+Math.round((bh-this.getMinimum())/this.getSingleStep())*this.getSingleStep();
}this.setValue(bh);
},
_applyOrientation:function(bh,
bo){var ba=this._getChildControl(a);
this.__ja=bh===b;
if(this.__ja){this.removeState(c);
ba.removeState(c);
this.addState(b);
ba.addState(b);
ba.setLayoutProperties({top:0,
right:null,
bottom:0});
}else{this.removeState(b);
ba.removeState(b);
this.addState(c);
ba.addState(c);
ba.setLayoutProperties({right:0,
bottom:null,
left:0});
}this._updateKnobPosition();
},
_applyKnobFactor:function(bh,
bo){if(bh!=null){this._updateKnobSize();
}else{if(this.__ja){this._getChildControl(a).resetWidth();
}else{this._getChildControl(a).resetHeight();
}}},
_applyValue:function(bh,
bo){this._updateKnobPosition();
},
_applyMinimum:function(bh,
bo){if(this.getValue()<bh){this.setValue(bh);
}this._updateKnobPosition();
},
_applyMaximum:function(bh,
bo){if(this.getValue()>bh){this.setValue(bh);
}this._updateKnobPosition();
}}});
})();
(function(){var a="mousewheel",
b="qx.ui.core.ScrollSlider",
c="keypress";
qx.Class.define(b,
{extend:qx.ui.form.Slider,
construct:function(d){arguments.callee.base.call(this,
d);
this.removeListener(c,
this._onKeyPress);
this.removeListener(a,
this._onMouseWheel);
}});
})();
(function(){var a="pressed",
b="abandoned",
c="Integer",
d="hovered",
f="qx.event.type.Event",
g="Enter",
h="Space",
i="press",
j="qx.ui.form.RepeatButton",
k="release",
l="interval",
m="__jc",
n="execute";
qx.Class.define(j,
{extend:qx.ui.form.Button,
construct:function(o,
p){arguments.callee.base.call(this,
o,
p);
this.__jc=new qx.event.Timer(this.getInterval());
this.__jc.addListener(l,
this._onInterval,
this);
},
events:{"execute":f,
"press":f,
"release":f},
properties:{interval:{check:c,
init:100},
firstInterval:{check:c,
init:500},
minTimer:{check:c,
init:20},
timerDecrease:{check:c,
init:2}},
members:{__jd:null,
__je:null,
__jc:null,
press:function(){if(this.isEnabled()){if(!this.hasState(a)){this.__jf();
}this.removeState(b);
this.addState(a);
}},
release:function(q){if(!this.isEnabled()){return;
}if(this.hasState(a)){if(!this.__je){this.execute();
}}this.removeState(a);
this.removeState(b);
this.__jg();
},
_applyEnabled:function(r,
s){arguments.callee.base.call(this,
r,
s);
if(!r){this.removeState(a);
this.removeState(b);
this.__jg();
}},
_onMouseOver:function(t){if(!this.isEnabled()||t.getTarget()!==this){return;
}
if(this.hasState(b)){this.removeState(b);
this.addState(a);
this.__jc.start();
}this.addState(d);
},
_onMouseOut:function(t){if(!this.isEnabled()||t.getTarget()!==this){return;
}this.removeState(d);
if(this.hasState(a)){this.removeState(a);
this.addState(b);
this.__jc.stop();
this.__jd=this.getInterval();
}},
_onMouseDown:function(t){if(!t.isLeftPressed()){return;
}this.capture();
this.__jf();
t.stopPropagation();
},
_onMouseUp:function(t){this.releaseCapture();
if(!this.hasState(b)){this.addState(d);
if(this.hasState(a)&&!this.__je){this.execute();
}}this.__jg();
t.stopPropagation();
},
_onKeyUp:function(t){switch(t.getKeyIdentifier()){case g:case h:if(this.hasState(a)){if(!this.__je){this.execute();
}this.removeState(a);
this.removeState(b);
t.stopPropagation();
this.__jg();
}}},
_onKeyDown:function(t){switch(t.getKeyIdentifier()){case g:case h:this.removeState(b);
this.addState(a);
t.stopPropagation();
this.__jf();
}},
_onInterval:function(t){this.__jc.stop();
if(this.__jd==null){this.__jd=this.getInterval();
}this.__jd=(Math.max(this.getMinTimer(),
this.__jd-this.getTimerDecrease()));
this.__jc.restartWith(this.__jd);
this.__je=true;
this.fireEvent(n);
},
__jf:function(){this.fireEvent(i);
this.__je=false;
this.__jc.setInterval(this.getFirstInterval());
this.__jc.start();
this.removeState(b);
this.addState(a);
},
__jg:function(){this.fireEvent(k);
this.__jc.stop();
this.__jd=null;
this.removeState(b);
this.removeState(a);
}},
destruct:function(){this._disposeObjects(m);
}});
})();
(function(){var a="qx.event.type.DataEvent",
b="qx.ui.table.ITableModel";
qx.Interface.define(b,
{events:{"dataChanged":a,
"metaDataChanged":a},
statics:{EVENT_TYPE_DATA_CHANGED:"dataChanged",
EVENT_TYPE_META_DATA_CHANGED:"metaDataChanged"},
members:{getRowCount:function(){},
getRowData:function(c){},
getColumnCount:function(){},
getColumnId:function(d){},
getColumnIndexById:function(e){},
getColumnName:function(d){},
isColumnEditable:function(d){},
isColumnSortable:function(d){},
sortByColumn:function(d,
f){},
getSortColumnIndex:function(){},
isSortAscending:function(){},
prefetchRows:function(g,
h){},
getValue:function(d,
c){},
getValueById:function(e,
c){},
setValue:function(d,
c,
i){},
setValueById:function(e,
c,
i){}}});
})();
(function(){var a="qx.event.type.DataEvent",
b="abstract",
c="__ji",
d="qx.ui.table.model.Abstract",
e="__jh",
f="__jj";
qx.Class.define(d,
{type:b,
extend:qx.core.Object,
implement:qx.ui.table.ITableModel,
events:{"dataChanged":a,
"metaDataChanged":a},
construct:function(){arguments.callee.base.call(this);
this.__jh=[];
this.__ji=[];
this.__jj={};
},
members:{__jh:null,
__ji:null,
__jj:null,
__jk:null,
getRowCount:function(){throw new Error("getRowCount is abstract");
},
getRowData:function(g){return null;
},
isColumnEditable:function(h){return false;
},
isColumnSortable:function(h){return false;
},
sortByColumn:function(h,
j){},
getSortColumnIndex:function(){return -1;
},
isSortAscending:function(){return true;
},
prefetchRows:function(k,
l){},
getValue:function(h,
g){throw new Error("getValue is abstract");
},
getValueById:function(m,
g){return this.getValue(this.getColumnIndexById(m),
g);
},
setValue:function(h,
g,
n){throw new Error("setValue is abstract");
},
setValueById:function(m,
g,
n){return this.setValue(this.getColumnIndexById(m),
g,
n);
},
getColumnCount:function(){return this.__jh.length;
},
getColumnIndexById:function(m){return this.__jj[m];
},
getColumnId:function(h){return this.__jh[h];
},
getColumnName:function(h){return this.__ji[h];
},
setColumnIds:function(o){this.__jh=o;
this.__jj={};
for(var p=0;p<o.length;p++){this.__jj[o[p]]=p;
}this.__ji=new Array(o.length);
if(!this.__jk){this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
}},
setColumnNamesByIndex:function(q){if(this.__jh.length!=q.length){throw new Error("this.__jh and columnNameArr have different length: "+this.__jh.length+" != "+q.length);
}this.__ji=q;
this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
},
setColumnNamesById:function(r){this.__ji=new Array(this.__jh.length);
for(var p=0;p<this.__jh.length;++p){this.__ji[p]=r[this.__jh[p]];
}},
setColumns:function(q,
o){if(o==null){o=q;
}
if(o.length!=q.length){throw new Error("columnIdArr and columnNameArr have different length: "+o.length+" != "+q.length);
}this.__jk=true;
this.setColumnIds(o);
this.__jk=false;
this.setColumnNamesByIndex(q);
}},
destruct:function(){this._disposeFields(e,
c,
f);
}});
})();
(function(){var a="qx.ui.table.model.Simple",
b="__jn",
c="__jo",
d="Boolean",
e="__jl",
f="__jp";
qx.Class.define(a,
{extend:qx.ui.table.model.Abstract,
construct:function(){arguments.callee.base.call(this);
this.__jl=[];
this.__jm=-1;
this.__jq;
this.__jn=[];
this.__jo=null;
},
properties:{caseSensitiveSorting:{check:d,
init:true}},
statics:{_defaultSortComparatorAscending:function(g,
h){var k=g[arguments.callee.columnIndex];
var l=h[arguments.callee.columnIndex];
return (k>l)?1:((k==l)?0:-1);
},
_defaultSortComparatorInsensitiveAscending:function(g,
h){var k=(isNaN(g[arguments.callee.columnIndex])?g[arguments.callee.columnIndex].toLowerCase():g[arguments.callee.columnIndex]);
var l=(isNaN(h[arguments.callee.columnIndex])?h[arguments.callee.columnIndex].toLowerCase():h[arguments.callee.columnIndex]);
return (k>l)?1:((k==l)?0:-1);
},
_defaultSortComparatorDescending:function(g,
h){var k=g[arguments.callee.columnIndex];
var l=h[arguments.callee.columnIndex];
return (k<l)?1:((k==l)?0:-1);
},
_defaultSortComparatorInsensitiveDescending:function(g,
h){var k=(isNaN(g[arguments.callee.columnIndex])?g[arguments.callee.columnIndex].toLowerCase():g[arguments.callee.columnIndex]);
var l=(isNaN(h[arguments.callee.columnIndex])?h[arguments.callee.columnIndex].toLowerCase():h[arguments.callee.columnIndex]);
return (k<l)?1:((k==l)?0:-1);
}},
members:{__jl:null,
__jo:null,
__jp:null,
__jn:null,
__jm:null,
__jq:null,
getRowData:function(m){var n=this.__jl[m];
if(n==null||n.originalData==null){return n;
}else{return n.originalData;
}},
getRowDataAsMap:function(m){var o=this.__jl[m];
var p={};
for(var q=0;q<this.getColumnCount();q++){p[this.getColumnId(q)]=o[q];
}return p;
},
setEditable:function(r){this.__jo=[];
for(var q=0;q<this.getColumnCount();q++){this.__jo[q]=r;
}this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
},
setColumnEditable:function(s,
r){if(r!=this.isColumnEditable(s)){if(this.__jo==null){this.__jo=[];
}this.__jo[s]=r;
this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
}},
isColumnEditable:function(s){return this.__jo?(this.__jo[s]==true):false;
},
setColumnSortable:function(s,
t){if(t!=this.isColumnSortable(s)){if(this.__jp==null){this.__jp=[];
}this.__jp[s]=t;
this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
}},
isColumnSortable:function(s){return (this.__jp?(this.__jp[s]!==false):true);
},
sortByColumn:function(s,
u){var v;
var w=this.__jn[s];
if(w){v=(u?w.ascending:w.descending);
}else{if(this.getCaseSensitiveSorting()){v=(u?qx.ui.table.model.Simple._defaultSortComparatorAscending:qx.ui.table.model.Simple._defaultSortComparatorDescending);
}else{v=(u?qx.ui.table.model.Simple._defaultSortComparatorInsensitiveAscending:qx.ui.table.model.Simple._defaultSortComparatorInsensitiveDescending);
}}v.columnIndex=s;
this.__jl.sort(v);
this.__jm=s;
this.__jq=u;
this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
},
setSortMethods:function(s,
x){this.__jn[s]=x;
},
clearSorting:function(){if(this.__jm!=-1){this.__jm=-1;
this.__jq=true;
this.fireEvent(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED);
}},
getSortColumnIndex:function(){return this.__jm;
},
isSortAscending:function(){return this.__jq;
},
getRowCount:function(){return this.__jl.length;
},
getValue:function(s,
m){if(m<0||m>=this.__jl.length){throw new Error("this.__jl out of bounds: "+m+" (0.."+this.__jl.length+")");
}return this.__jl[m][s];
},
setValue:function(s,
m,
y){if(this.__jl[m][s]!=y){this.__jl[m][s]=y;
if(this.hasListener(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED)){var z={firstRow:m,
lastRow:m,
firstColumn:s,
lastColumn:s};
this.fireDataEvent(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
z);
}
if(s==this.__jm){this.clearSorting();
}}},
setData:function(A,
B){this.__jl=A;
if(this.hasListener(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED)){var z={firstRow:0,
lastRow:A.length-1,
firstColumn:0,
lastColumn:this.getColumnCount()-1};
this.fireDataEvent(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
z);
}
if(B!==false){this.clearSorting();
}},
getData:function(){return this.__jl;
},
setDataAsMapArray:function(C,
D,
B){this.setData(this._mapArray2RowArr(C,
D),
B);
},
addRows:function(A,
E,
B){if(E==null){E=this.__jl.length;
}A.splice(0,
0,
E,
0);
Array.prototype.splice.apply(this.__jl,
A);
var z={firstRow:E,
lastRow:this.__jl.length-1,
firstColumn:0,
lastColumn:this.getColumnCount()-1};
this.fireDataEvent(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
z);
if(B!==false){this.clearSorting();
}},
addRowsAsMapArray:function(C,
E,
D,
B){this.addRows(this._mapArray2RowArr(C,
D),
E,
B);
},
removeRows:function(E,
F,
B){this.__jl.splice(E,
F);
var z={firstRow:E,
lastRow:this.__jl.length-1,
firstColumn:0,
lastColumn:this.getColumnCount()-1,
removeStart:E,
removeCount:F};
this.fireDataEvent(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
z);
if(B!==false){this.clearSorting();
}},
_mapArray2RowArr:function(C,
D){var G=C.length;
var H=this.getColumnCount();
var I=new Array(G);
var o;
for(var J=0;J<G;++J){o=[];
if(D){o.originalData=C[J];
}
for(var K=0;K<H;++K){o[K]=C[J][this.getColumnId(K)];
}I[J]=o;
}return I;
}},
destruct:function(){this._disposeFields(e,
c,
b,
f);
}});
})();
(function(){var a="Function",
b="Boolean",
c="qx.event.type.DataEvent",
d="qx.ui.table.pane.CellEvent",
e="PageUp",
f="column-button",
g="excluded",
h="qx.dynamicLocaleSwitch",
j="changeSelection",
k="visible",
m="Enter",
n="changeLocale",
o="statusbar",
p="on",
q="_applyTableColumnModel",
r="_applyStatusBarVisible",
s="blur",
t="qx.ui.table.Table",
u="columnVisibilityMenuCreateEnd",
v="verticalScrollBarChanged",
w="_applyMetaColumnCounts",
y="one of one row",
z="focus",
A="changeDataRowRenderer",
B="changeHeaderCellHeight",
C="Escape",
D="changeSelectionModel",
E="Left",
F="_tableModel",
G="Down",
H="Integer",
I="_applyHeaderCellHeight",
J="Object",
K="__ju",
L="visibilityChanged",
M="qx.ui.table.ITableModel",
N="orderChanged",
O="_applySelectionModel",
P="__js",
Q="_columnVisibilityMenu",
R="_applyAdditionalStatusBarText",
S="_applyFocusCellOnMouseMove",
T="table",
U="_applyColumnVisibilityButtonVisible",
V="changeTableModel",
W="qx.event.type.Event",
X="A",
Y="End",
ba="_applyShowCellFocusIndicator",
bb="resize",
bc="changeScrollY",
bd="_applyTableModel",
be="_applyKeepFirstVisibleRowComplete",
bf="qx.ui.table.columnmodel.Basic",
bg="Home",
bh="_applyRowHeight",
bi="F2",
bj="Up",
bk="tableWidthChanged",
bl="columnVisibilityMenuCreateStart",
bm="%1 rows",
bn="qx.ui.table.selection.Model",
bo="one row",
bp="__jr",
bq="PageDown",
br="%1 of %2 rows",
bs="changeTableColumnModel",
bt="keypress",
bu="changeRowHeight",
bv="__jt",
bw="Number",
bx="widthChanged",
by="changeChecked",
bz="qx.ui.table.IRowRenderer",
bA="Right",
bB="Space";
qx.Class.define(t,
{extend:qx.ui.core.Widget,
construct:function(bC,
bD){arguments.callee.base.call(this);
if(!bD){bD={};
}
if(bD.selectionManager){this.setNewSelectionManager(bD.selectionManager);
}
if(bD.selectionModel){this.setNewSelectionModel(bD.selectionModel);
}
if(bD.tableColumnModel){this.setNewTableColumnModel(bD.tableColumnModel);
}
if(bD.tablePane){this.setNewTablePane(bD.tablePane);
}
if(bD.tablePaneHeader){this.setNewTablePaneHeader(bD.tablePaneHeader);
}
if(bD.tablePaneScroller){this.setNewTablePaneScroller(bD.tablePaneScroller);
}
if(bD.tablePaneModel){this.setNewTablePaneModel(bD.tablePaneModel);
}this._setLayout(new qx.ui.layout.VBox());
this.__jr=new qx.ui.container.Composite(new qx.ui.layout.HBox());
this.__js=this._getChildControl(o);
this._add(this.__jr,
{flex:1});
this._add(this.__js);
this.__jt=this._getChildControl(f);
this.setDataRowRenderer(new qx.ui.table.rowrenderer.Default(this));
this.__ju=this.getNewSelectionManager()(this);
this.setSelectionModel(this.getNewSelectionModel()(this));
this.setTableColumnModel(this.getNewTableColumnModel()(this));
if(bC!=null){this.setTableModel(bC);
}this.setMetaColumnCounts([-1]);
this.setTabIndex(1);
this.addListener(bt,
this._onKeyPress);
this.addListener(z,
this._onFocusChanged);
this.addListener(s,
this._onFocusChanged);
var bE=new qx.ui.core.Widget().set({height:0});
this._add(bE);
bE.addListener(bb,
this._onResize,
this);
this.__jv=null;
this.__jw=null;
if(qx.core.Variant.isSet(h,
p)){qx.locale.Manager.getInstance().addListener(n,
this._onChangeLocale,
this);
}},
events:{"columnVisibilityMenuCreateStart":c,
"columnVisibilityMenuCreateEnd":c,
"tableWidthChanged":W,
"verticalScrollBarChanged":c,
"cellClick":d,
"cellDblclick":d,
"cellContextmenu":d},
statics:{__jx:{cellClick:1,
cellDblclick:1,
cellContextmenu:1}},
properties:{appearance:{refine:true,
init:T},
focusable:{refine:true,
init:true},
selectionModel:{check:bn,
apply:O,
event:D},
tableModel:{check:M,
apply:bd,
event:V,
nullable:true},
tableColumnModel:{check:bf,
apply:q,
event:bs},
rowHeight:{check:bw,
init:20,
apply:bh,
event:bu},
forceLineHeight:{check:b,
init:true},
headerCellHeight:{check:H,
init:16,
apply:I,
event:B},
statusBarVisible:{check:b,
init:true,
apply:r},
additionalStatusBarText:{nullable:true,
init:null,
apply:R},
columnVisibilityButtonVisible:{check:b,
init:true,
apply:U},
metaColumnCounts:{check:J,
apply:w},
focusCellOnMouseMove:{check:b,
init:false,
apply:S},
showCellFocusIndicator:{check:b,
init:true,
apply:ba},
keepFirstVisibleRowComplete:{check:b,
init:true,
apply:be},
alwaysUpdateCells:{check:b,
init:false},
dataRowRenderer:{check:bz,
init:null,
nullable:true,
event:A},
modalCellEditorPreOpenFunction:{check:a,
init:null,
nullable:true},
newSelectionManager:{check:a,
init:function(bF){return new qx.ui.table.selection.Manager(bF);
}},
newSelectionModel:{check:a,
init:function(bF){return new qx.ui.table.selection.Model(bF);
}},
newTableColumnModel:{check:a,
init:function(bF){return new qx.ui.table.columnmodel.Basic(bF);
}},
newTablePane:{check:a,
init:function(bF){return new qx.ui.table.pane.Pane(bF);
}},
newTablePaneHeader:{check:a,
init:function(bF){return new qx.ui.table.pane.Header(bF);
}},
newTablePaneScroller:{check:a,
init:function(bF){return new qx.ui.table.pane.Scroller(bF);
}},
newTablePaneModel:{check:a,
init:function(bG){return new qx.ui.table.pane.Model(bG);
}}},
members:{__jv:null,
__jw:null,
__jr:null,
__js:null,
__jt:null,
__ju:null,
__jy:null,
__jz:null,
__jA:null,
_createChildControlImpl:function(bH){var bI;
switch(bH){case o:bI=new qx.ui.basic.Label().set({allowGrowX:true});
break;
case f:bI=new qx.ui.form.MenuButton().set({focusable:false});
break;
}return bI||arguments.callee.base.call(this,
bH);
},
_applySelectionModel:function(bJ,
bK){this.__ju.setSelectionModel(bJ);
if(bK!=null){bK.removeListener(j,
this._onSelectionChanged,
this);
}bJ.addListener(j,
this._onSelectionChanged,
this);
},
_applyRowHeight:function(bJ,
bK){if(!this.getTableModel()){return;
}var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].updateVerScrollBarMaximum();
}},
_applyHeaderCellHeight:function(bJ,
bK){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].getHeader().setHeight(bJ);
}},
_applyTableModel:function(bJ,
bK){this.getTableColumnModel().init(bJ.getColumnCount(),
this);
if(bK!=null){bK.removeListener(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED,
this._onTableModelMetaDataChanged,
this);
bK.removeListener(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
this._onTableModelDataChanged,
this);
}bJ.addListener(qx.ui.table.ITableModel.EVENT_TYPE_META_DATA_CHANGED,
this._onTableModelMetaDataChanged,
this);
bJ.addListener(qx.ui.table.ITableModel.EVENT_TYPE_DATA_CHANGED,
this._onTableModelDataChanged,
this);
this._updateStatusBar();
this._initColumnMenu();
},
_applyTableColumnModel:function(bJ,
bK){if(bK!=null){throw new Error("The table column model can only be set once per table.");
}bJ.addListener(L,
this._onColVisibilityChanged,
this);
bJ.addListener(bx,
this._onColWidthChanged,
this);
bJ.addListener(N,
this._onColOrderChanged,
this);
var bN=this.getTableModel();
if(bN){bJ.init(bN.getColumnCount(),
this);
}var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){var bO=bL[bM];
var bP=bO.getTablePaneModel();
bP.setTableColumnModel(bJ);
}},
_applyStatusBarVisible:function(bJ,
bK){this.__js.setVisibility(bJ?k:g);
if(bJ){this._updateStatusBar();
}},
_applyAdditionalStatusBarText:function(bJ,
bK){this.__jy=bJ;
this._updateStatusBar();
},
_applyColumnVisibilityButtonVisible:function(bJ,
bK){this.__jt.setVisibility(bJ?k:g);
},
_applyMetaColumnCounts:function(bJ,
bK){var bQ=bJ;
var bL=this._getPaneScrollerArr();
this._cleanUpMetaColumns(bQ.length);
var bR=0;
for(var bM=0;bM<bL.length;bM++){var bO=bL[bM];
var bP=bO.getTablePaneModel();
bP.setFirstColumnX(bR);
bP.setMaxColumnCount(bQ[bM]);
bR+=bQ[bM];
}if(bQ.length>bL.length){var bG=this.getTableColumnModel();
for(var bM=bL.length;bM<bQ.length;bM++){var bP=this.getNewTablePaneModel()(bG);
bP.setFirstColumnX(bR);
bP.setMaxColumnCount(bQ[bM]);
bR+=bQ[bM];
var bO=this.getNewTablePaneScroller()(this);
bO.setTablePaneModel(bP);
bO.addListener(bc,
this._onScrollY,
this);
var bS=(bM==bQ.length-1)?1:0;
this.__jr.add(bO,
{flex:bS});
bL=this._getPaneScrollerArr();
}}for(var bM=0;bM<bL.length;bM++){var bO=bL[bM];
var bT=(bM==(bL.length-1));
bO.getHeader().setHeight(this.getHeaderCellHeight());
bO.setTopRightWidget(bT?this.__jt:null);
}this._updateScrollerWidths();
this._updateScrollBarVisibility();
},
_applyFocusCellOnMouseMove:function(bJ,
bK){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].setFocusCellOnMouseMove(bJ);
}},
_applyShowCellFocusIndicator:function(bJ,
bK){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].setShowCellFocusIndicator(bJ);
}},
_applyKeepFirstVisibleRowComplete:function(bJ,
bK){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onKeepFirstVisibleRowCompleteChanged();
}},
getSelectionManager:function(){return this.__ju;
},
_getPaneScrollerArr:function(){return this.__jr.getChildren();
},
getPaneScroller:function(bU){return this._getPaneScrollerArr()[bU];
},
_cleanUpMetaColumns:function(bV){var bL=this._getPaneScrollerArr();
if(bL!=null){for(var bM=bL.length-1;bM>=bV;bM--){bL[bM].dispose();
}}},
_onChangeLocale:function(bW){this.updateContent();
this._updateStatusBar();
},
_onSelectionChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onSelectionChanged();
}this._updateStatusBar();
},
_onTableModelMetaDataChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onTableModelMetaDataChanged();
}this._updateStatusBar();
},
_onTableModelDataChanged:function(bW){var bL=this._getPaneScrollerArr();
var bX=bW.getData();
if(bX.removeCount){this.getSelectionModel().removeSelectionInterval(bX.removeStart,
bX.removeStart+bX.removeCount);
}
for(var bM=0;bM<bL.length;bM++){bL[bM].onTableModelDataChanged(bX.firstRow,
bX.lastRow,
bX.firstColumn,
bX.lastColumn);
}var bY=this.getTableModel().getRowCount();
if(bY!=this.__jz){this.__jz=bY;
this._updateScrollBarVisibility();
this._updateStatusBar();
}},
_onScrollY:function(bW){if(!this.__jA){this.__jA=true;
var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].setScrollY(bW.getData());
}this.__jA=false;
}},
_onKeyPress:function(bW){if(!this.getEnabled()){return;
}var ca=this.__jw;
var cb=true;
var cc=bW.getKeyIdentifier();
if(this.isEditing()){if(bW.getModifiers()==0){switch(cc){case m:this.stopEditing();
var ca=this.__jw;
this.moveFocusedCell(0,
1);
if(this.__jw!=ca){cb=this.startEditing();
}break;
case C:this.cancelEditing();
this.focus();
break;
default:cb=false;
break;
}}return;
}else{if(bW.isCtrlPressed()){cb=true;
switch(cc){case X:var bY=this.getTableModel().getRowCount();
if(bY>0){this.getSelectionModel().setSelectionInterval(0,
bY-1);
}break;
default:cb=false;
break;
}}else{switch(cc){case bB:this.__ju.handleSelectKeyDown(this.__jw,
bW);
break;
case bi:case m:cb=this.startEditing();
break;
case bg:this.setFocusedCell(this.__jv,
0,
true);
break;
case Y:var bY=this.getTableModel().getRowCount();
this.setFocusedCell(this.__jv,
bY-1,
true);
break;
case E:this.moveFocusedCell(-1,
0);
break;
case bA:this.moveFocusedCell(1,
0);
break;
case bj:this.moveFocusedCell(0,
-1);
break;
case G:this.moveFocusedCell(0,
1);
break;
case e:case bq:var cd=this.getPaneScroller(0);
var ce=cd.getTablePane();
var bY=ce.getVisibleRowCount()-1;
var cf=this.getRowHeight();
var cg=(cc==e)?-1:1;
cd.setScrollY(cd.getScrollY()+cg*bY*cf);
this.moveFocusedCell(0,
cg*bY);
break;
default:cb=false;
}}}
if(ca!=this.__jw){this.__ju.handleMoveKeyDown(this.__jw,
bW);
}
if(cb){bW.preventDefault();
bW.stopPropagation();
}},
_onFocusChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onFocusChanged();
}},
_onColVisibilityChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onColVisibilityChanged();
}this._updateScrollerWidths();
this._updateScrollBarVisibility();
},
_onColWidthChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){var bX=bW.getData();
bL[bM].setColumnWidth(bX.col,
bX.newWidth);
}this._updateScrollerWidths();
this._updateScrollBarVisibility();
},
_onColOrderChanged:function(bW){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].onColOrderChanged();
}this._updateScrollerWidths();
this._updateScrollBarVisibility();
},
getTablePaneScrollerAtPageX:function(ch){var ci=this._getMetaColumnAtPageX(ch);
return (ci!=-1)?this.getPaneScroller(ci):null;
},
setFocusedCell:function(cj,
ck,
cl){if(!this.isEditing()&&(cj!=this.__jv||ck!=this.__jw)){this.__jv=cj;
this.__jw=ck;
var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].setFocusedCell(cj,
ck);
}
if(cj!==null&&cl){this.scrollCellVisible(cj,
ck);
}}},
clearSelection:function(){this.getSelectionModel().clearSelection();
},
resetCellFocus:function(){this.setFocusedCell(null,
null,
false);
},
getFocusedColumn:function(){return this.__jv;
},
getFocusedRow:function(){return this.__jw;
},
moveFocusedCell:function(cm,
cn){var cj=this.__jv;
var ck=this.__jw;
if(cj===null||ck===null){return;
}
if(cm!=0){var bG=this.getTableColumnModel();
var co=bG.getVisibleX(cj);
var cp=bG.getVisibleColumnCount();
co=qx.lang.Number.limit(co+cm,
0,
cp-1);
cj=bG.getVisibleColumnAtX(co);
}
if(cn!=0){var bC=this.getTableModel();
ck=qx.lang.Number.limit(ck+cn,
0,
bC.getRowCount()-1);
}this.setFocusedCell(cj,
ck,
true);
},
scrollCellVisible:function(cj,
ck){var bG=this.getTableColumnModel();
var co=bG.getVisibleX(cj);
var bU=this._getMetaColumnAtColumnX(co);
if(bU!=-1){this.getPaneScroller(bU).scrollCellVisible(cj,
ck);
}},
isEditing:function(){if(this.__jv!=null){var co=this.getTableColumnModel().getVisibleX(this.__jv);
var bU=this._getMetaColumnAtColumnX(co);
return this.getPaneScroller(bU).isEditing();
}return false;
},
startEditing:function(){if(this.__jv!=null){var co=this.getTableColumnModel().getVisibleX(this.__jv);
var bU=this._getMetaColumnAtColumnX(co);
var cq=this.getPaneScroller(bU).startEditing();
return cq;
}return false;
},
stopEditing:function(){if(this.__jv!=null){var co=this.getTableColumnModel().getVisibleX(this.__jv);
var bU=this._getMetaColumnAtColumnX(co);
this.getPaneScroller(bU).stopEditing();
}},
cancelEditing:function(){if(this.__jv!=null){var co=this.getTableColumnModel().getVisibleX(this.__jv);
var bU=this._getMetaColumnAtColumnX(co);
this.getPaneScroller(bU).cancelEditing();
}},
updateContent:function(){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){bL[bM].getTablePane().updateContent();
}},
_getMetaColumnAtPageX:function(ch){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){var cr=bL[bM].getContainerLocation();
if(ch>=cr.left&&ch<=cr.right){return bM;
}}return -1;
},
_getMetaColumnAtColumnX:function(cs){var bQ=this.getMetaColumnCounts();
var ct=0;
for(var bM=0;bM<bQ.length;bM++){var cu=bQ[bM];
ct+=cu;
if(cu==-1||cs<ct){return bM;
}}return -1;
},
_updateStatusBar:function(){if(this.getStatusBarVisible()){var cv=this.getSelectionModel().getSelectedCount();
var bY=this.getTableModel().getRowCount();
var cw;
if(bY>0){if(cv==0){cw=this.trn(bo,
bm,
bY,
bY);
}else{cw=this.trn(y,
br,
bY,
cv,
bY);
}}
if(this.__jy){if(cw){cw+=this.__jy;
}else{cw=this.__jy;
}}
if(cw){this.__js.setContent(cw);
}}},
_updateScrollerWidths:function(){var bL=this._getPaneScrollerArr();
for(var bM=0;bM<bL.length;bM++){var bT=(bM==(bL.length-1));
var cx=bL[bM].getTablePaneModel().getTotalWidth();
bL[bM].setWidth(cx);
var bS=bT?1:0;
bL[bM].setLayoutProperties({flex:bS});
}},
_updateScrollBarVisibility:function(){if(!this.getBounds()){return;
}var cy=qx.ui.table.pane.Scroller.HORIZONTAL_SCROLLBAR;
var cz=qx.ui.table.pane.Scroller.VERTICAL_SCROLLBAR;
var bL=this._getPaneScrollerArr();
var cA=false;
var cB=false;
for(var bM=0;bM<bL.length;bM++){var bT=(bM==(bL.length-1));
var cC=bL[bM].getNeededScrollBars(cA,
!bT);
if(cC&cy){cA=true;
}
if(bT&&(cC&cz)){cB=true;
}}for(var bM=0;bM<bL.length;bM++){var bT=(bM==(bL.length-1));
var cD;
bL[bM].setHorizontalScrollBarVisible(cA);
if(bT){cD=bL[bM].getVerticalScrollBarVisible();
}bL[bM].setVerticalScrollBarVisible(bT&&cB);
if(bT&&cB!=cD){this.fireDataEvent(v,
cB);
}}},
_initColumnMenu:function(){var bC=this.getTableModel();
var bG=this.getTableColumnModel();
var cE=this.__jt.getMenu();
if(cE){var cF=cE.getChildren();
for(var bM=0,
cG=cF.length;bM<cG;bM++){cF[bM].destroy();
}}else{var cE=new qx.ui.menu.Menu();
this.__jt.setMenu(cE);
}var bX={table:this,
menu:cE};
this.fireDataEvent(bl,
bX);
for(var cj=0,
cG=bC.getColumnCount();cj<cG;cj++){var cH=new qx.ui.menu.CheckBox(bC.getColumnName(cj));
cH.setChecked(bG.isColumnVisible(cj));
cH.addListener(by,
this._createColumnVisibilityCheckBoxHandler(cj),
this);
cE.add(cH);
}var bX={table:this,
menu:cE};
this.fireDataEvent(u,
bX);
},
_createColumnVisibilityCheckBoxHandler:function(cj){return function(bW){var bG=this.getTableColumnModel();
bG.setColumnVisible(cj,
bW.getData());
};
},
setColumnWidth:function(cj,
cx){this.getTableColumnModel().setColumnWidth(cj,
cx);
},
_onResize:function(){this.fireEvent(bk);
this._updateScrollerWidths();
this._updateScrollBarVisibility();
},
addListener:function(cI,
cJ,
cK,
cL){if(arguments.callee.self.__jx[cI]){for(var bM=0,
cM=this._getPaneScrollerArr();bM<cM.length;bM++){cM[bM].addListener.apply(cM[bM],
arguments);
}}else{arguments.callee.base.call(this,
cI,
cJ,
cK,
cL);
}},
removeListener:function(cI,
cJ,
cK,
cL){if(arguments.callee.self.__jx[cI]){for(var bM=0,
cM=this._getPaneScrollerArr();bM<cM.length;bM++){cM[bM].removeListener.apply(cM[bM],
arguments);
}}else{arguments.callee.base.call(this,
cI,
cJ,
cK,
cL);
}}},
destruct:function(){if(qx.core.Variant.isSet(h,
p)){qx.locale.Manager.getInstance().removeListener(n,
this._onChangeLocale,
this);
}var cN=this.getSelectionModel();
if(cN){cN.dispose();
}var cO=this.getDataRowRenderer();
if(cO){cO.dispose();
}this._cleanUpMetaColumns(0);
this._disposeObjects(K,
Q,
F,
bv,
bp,
P);
}});
})();
(function(){var a="qx.ui.table.IRowRenderer";
qx.Interface.define(a,
{members:{updateDataRowElement:function(b,
c){},
getRowHeightStyle:function(d){},
createRowStyle:function(b){},
getRowClass:function(b){}}});
})();
(function(){var a="",
b="table-row-background-even",
c="Boolean",
d="__jC",
e="default",
f="height:",
g="'",
h="table-row",
i="table-row-background-focused",
j=';color:',
k="table-row-background-odd",
l="1px solid ",
m="table-row-line",
n="table-row-background-selected",
o="background-color:",
p="__jB",
q=';border-bottom: 1px solid ',
r="table-row-selected",
s="table-row-background-focused-selected",
t="__jD",
u="px;",
v="qx.ui.table.rowrenderer.Default",
w=";";
qx.Class.define(v,
{extend:qx.core.Object,
implement:qx.ui.table.IRowRenderer,
construct:function(){arguments.callee.base.call(this);
this.__jB=a;
this.__jB={};
this.__jC={};
this._renderFont(qx.theme.manager.Font.getInstance().resolve(e));
var x=qx.theme.manager.Color.getInstance();
this.__jC.bgcolFocusedSelected=x.resolve(s);
this.__jC.bgcolFocused=x.resolve(i);
this.__jC.bgcolSelected=x.resolve(n);
this.__jC.bgcolEven=x.resolve(b);
this.__jC.bgcolOdd=x.resolve(k);
this.__jC.colSelected=x.resolve(r);
this.__jC.colNormal=x.resolve(h);
this.__jC.horLine=x.resolve(m);
},
properties:{highlightFocusRow:{check:c,
init:true}},
members:{__jC:null,
__jD:null,
__jB:null,
_insetY:1,
_renderFont:function(y){if(y){this.__jD=y.getStyles();
this.__jB=qx.bom.element.Style.compile(this.__jD);
this.__jB=this.__jB.replace(/"/g,
g);
}else{this.__jB=a;
this.__jD=qx.bom.Font.getDefaultStyles();
}},
updateDataRowElement:function(z,
A){var B=this.__jD;
var C=A.style;
qx.bom.element.Style.setStyles(A,
B);
if(z.focusedRow&&this.getHighlightFocusRow()){C.backgroundColor=z.selected?this.__jC.bgcolFocusedSelected:this.__jC.bgcolFocused;
}else{if(z.selected){C.backgroundColor=this.__jC.bgcolSelected;
}else{C.backgroundColor=(z.row%2==0)?this.__jC.bgcolEven:this.__jC.bgcolOdd;
}}C.color=z.selected?this.__jC.colSelected:this.__jC.colNormal;
C.borderBottom=l+this.__jC.horLine;
},
getRowHeightStyle:function(D){if(qx.bom.client.Feature.CONTENT_BOX){D-=this._insetY;
}return f+D+u;
},
createRowStyle:function(z){var E=[];
E.push(w);
E.push(this.__jB);
E.push(o);
if(z.focusedRow&&this.getHighlightFocusRow()){E.push(z.selected?this.__jC.bgcolFocusedSelected:this.__jC.bgcolFocused);
}else{if(z.selected){E.push(this.__jC.bgcolSelected);
}else{E.push((z.row%2==0)?this.__jC.bgcolEven:this.__jC.bgcolOdd);
}}E.push(j);
E.push(z.selected?this.__jC.colSelected:this.__jC.colNormal);
E.push(q,
this.__jC.horLine);
return E.join(a);
},
getRowClass:function(z){return a;
}},
destruct:function(){this._disposeFields(d,
t,
p);
}});
})();
(function(){var a="qx.ui.table.selection.Model",
b="qx.ui.table.selection.Manager";
qx.Class.define(b,
{extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
},
properties:{selectionModel:{check:a}},
members:{__jE:null,
handleMouseDown:function(c,
d){if(d.isLeftPressed()){var e=this.getSelectionModel();
if(!e.isSelectedIndex(c)){this._handleSelectEvent(c,
d);
this.__jE=true;
}else{this.__jE=false;
}}else if(d.isRightPressed()&&d.getModifiers()==0){var e=this.getSelectionModel();
if(!e.isSelectedIndex(c)){e.setSelectionInterval(c,
c);
}}},
handleMouseUp:function(c,
d){if(d.isLeftPressed()&&!this.__jE){this._handleSelectEvent(c,
d);
}},
handleClick:function(c,
d){},
handleSelectKeyDown:function(c,
d){this._handleSelectEvent(c,
d);
},
handleMoveKeyDown:function(c,
d){var e=this.getSelectionModel();
switch(d.getModifiers()){case 0:e.setSelectionInterval(c,
c);
break;
case qx.event.type.Dom.SHIFT_MASK:var f=e.getAnchorSelectionIndex();
if(f==-1){e.setSelectionInterval(c,
c);
}else{e.setSelectionInterval(f,
c);
}break;
}},
_handleSelectEvent:function(c,
d){var e=this.getSelectionModel();
var g=e.getLeadSelectionIndex();
var h=e.getAnchorSelectionIndex();
if(d.isShiftPressed()){if(c!=g||e.isSelectionEmpty()){if(h==-1){h=c;
}
if(d.isCtrlOrCommandPressed()){e.addSelectionInterval(h,
c);
}else{e.setSelectionInterval(h,
c);
}}}else if(d.isCtrlOrCommandPressed()){if(e.isSelectedIndex(c)){e.removeSelectionInterval(c,
c);
}else{e.addSelectionInterval(c,
c);
}}else{if(!(h==g&&h==c&&e.getSelectedCount()==1)){e.setSelectionInterval(c,
c);
}}}}});
})();
(function(){var a="..",
b="__jF",
c="changeSelection",
d="_applySelectionMode",
e="]",
f="qx.event.type.Event",
g="Ranges:",
h="qx.ui.table.selection.Model",
k=" [";
qx.Class.define(h,
{extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
this.__jF=[];
this.__jG=-1;
this.__jH=-1;
this.hasBatchModeRefCount=0;
this.__jI=false;
},
events:{"changeSelection":f},
statics:{NO_SELECTION:1,
SINGLE_SELECTION:2,
SINGLE_INTERVAL_SELECTION:3,
MULTIPLE_INTERVAL_SELECTION:4,
MULTIPLE_INTERVAL_SELECTION_TOGGLE:5},
properties:{selectionMode:{init:2,
check:[1,
2,
3,
4,
5],
apply:d}},
members:{__jI:null,
__jG:null,
__jH:null,
__jF:null,
_applySelectionMode:function(l){this.clearSelection();
},
setBatchMode:function(m){if(m){this.hasBatchModeRefCount+=1;
}else{if(this.hasBatchModeRefCount==0){throw new Error("Try to turn off batch mode althoug it was not turned on.");
}this.hasBatchModeRefCount-=1;
if(this.__jI){this.__jI=false;
this._fireChangeSelection();
}}return this.hasBatchMode();
},
hasBatchMode:function(){return this.hasBatchModeRefCount>0;
},
getAnchorSelectionIndex:function(){return this.__jG;
},
getLeadSelectionIndex:function(){return this.__jH;
},
clearSelection:function(){if(!this.isSelectionEmpty()){this._clearSelection();
this._fireChangeSelection();
}},
isSelectionEmpty:function(){return this.__jF.length==0;
},
getSelectedCount:function(){var n=0;
for(var o=0;o<this.__jF.length;o++){var p=this.__jF[o];
n+=p.maxIndex-p.minIndex+1;
}return n;
},
isSelectedIndex:function(q){for(var o=0;o<this.__jF.length;o++){var p=this.__jF[o];
if(q>=p.minIndex&&q<=p.maxIndex){return true;
}}return false;
},
getSelectedRanges:function(){var r=[];
for(var o=0;o<this.__jF.length;o++){r.push({minIndex:this.__jF[o].minIndex,
maxIndex:this.__jF[o].maxIndex});
}return r;
},
iterateSelection:function(s,
t){for(var o=0;o<this.__jF.length;o++){for(var u=this.__jF[o].minIndex;u<=this.__jF[o].maxIndex;u++){s.call(t,
u);
}}},
setSelectionInterval:function(v,
w){var x=arguments.callee.self;
switch(this.getSelectionMode()){case x.NO_SELECTION:return;
case x.SINGLE_SELECTION:v=w;
break;
case x.MULTIPLE_INTERVAL_SELECTION_TOGGLE:this.setBatchMode(true);
try{for(var o=v;o<=w;o++){if(!this.isSelectedIndex(o)){this._addSelectionInterval(o,
o);
}else{this.removeSelectionInterval(o,
o);
}}}finally{this.setBatchMode(false);
}this._fireChangeSelection();
return;
}this._clearSelection();
this._addSelectionInterval(v,
w);
this._fireChangeSelection();
},
addSelectionInterval:function(v,
w){var y=qx.ui.table.selection.Model;
switch(this.getSelectionMode()){case y.NO_SELECTION:return;
case y.MULTIPLE_INTERVAL_SELECTION:case y.MULTIPLE_INTERVAL_SELECTION_TOGGLE:this._addSelectionInterval(v,
w);
this._fireChangeSelection();
break;
default:this.setSelectionInterval(v,
w);
break;
}},
removeSelectionInterval:function(v,
w){this.__jG=v;
this.__jH=w;
var z=Math.min(v,
w);
var A=Math.max(v,
w);
for(var o=0;o<this.__jF.length;o++){var p=this.__jF[o];
if(p.minIndex>A){break;
}else if(p.maxIndex>=z){var B=(p.minIndex>=z)&&(p.minIndex<=A);
var C=(p.maxIndex>=z)&&(p.maxIndex<=A);
if(B&&C){this.__jF.splice(o,
1);
o--;
}else if(B){p.minIndex=A+1;
}else if(C){p.maxIndex=z-1;
}else{var D={minIndex:A+1,
maxIndex:p.maxIndex};
this.__jF.splice(o+1,
0,
D);
p.maxIndex=z-1;
break;
}}}this._fireChangeSelection();
},
_clearSelection:function(){this.__jF=[];
this.__jG=-1;
this.__jH=-1;
},
_addSelectionInterval:function(v,
w){this.__jG=v;
this.__jH=w;
var z=Math.min(v,
w);
var A=Math.max(v,
w);
var E=0;
for(;E<this.__jF.length;E++){var p=this.__jF[E];
if(p.minIndex>z){break;
}}this.__jF.splice(E,
0,
{minIndex:z,
maxIndex:A});
var F=this.__jF[0];
for(var o=1;o<this.__jF.length;o++){var p=this.__jF[o];
if(F.maxIndex+1>=p.minIndex){F.maxIndex=Math.max(F.maxIndex,
p.maxIndex);
this.__jF.splice(o,
1);
o--;
}else{F=p;
}}},
_dumpRanges:function(){var G=g;
for(var o=0;o<this.__jF.length;o++){var p=this.__jF[o];
G+=k+p.minIndex+a+p.maxIndex+e;
}this.debug(G);
},
_fireChangeSelection:function(){if(this.hasBatchMode()){this.__jI=true;
}this.fireEvent(c);
}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="qx.ui.table.IHeaderRenderer";
qx.Interface.define(a,
{members:{createHeaderCell:function(b){return true;
},
updateHeaderCell:function(b,
c){return true;
}}});
})();
(function(){var a="qx.ui.table.headerrenderer.Default",
b="String";
qx.Class.define(a,
{extend:qx.core.Object,
implement:qx.ui.table.IHeaderRenderer,
statics:{STATE_SORTED:"sorted",
STATE_SORTED_ASCENDING:"sortedAscending"},
properties:{toolTip:{check:b,
init:null,
nullable:true}},
members:{createHeaderCell:function(c){var d=new qx.ui.table.headerrenderer.HeaderCell();
this.updateHeaderCell(c,
d);
return d;
},
updateHeaderCell:function(c,
e){var f=qx.ui.table.headerrenderer.Default;
e.setLabel(c.name);
var g=e.getToolTip();
if(this.getToolTip()!=null){if(g==null){g=new qx.ui.tooltip.ToolTip(this.getToolTip());
e.setToolTip(g);
}else{g.setLabel(this.getToolTip());
}}c.sorted?e.addState(f.STATE_SORTED):e.removeState(f.STATE_SORTED);
c.sortedAscending?e.addState(f.STATE_SORTED_ASCENDING):e.removeState(f.STATE_SORTED_ASCENDING);
}}});
})();
(function(){var a="qx.ui.table.ICellRenderer";
qx.Interface.define(a,
{members:{createDataCellHtml:function(b,
c){return true;
}}});
})();
(function(){var a="",
b="px;",
c=".qooxdoo-table-cell {",
d="qooxdoo-table-cell",
e='</div>',
f="nowrap",
g="default",
h="qx.client",
i="}",
j="width:",
k=".qooxdoo-table-cell-right { text-align:right } ",
l="0px 6px",
m='<div class="',
n="0px",
o="height:",
p="1px solid ",
q=".qooxdoo-table-cell-bold { font-weight:bold } ",
r="table-row-line",
s="mshtml",
t="ellipsis",
u='">',
v="content-box",
w='left:',
x="qx.ui.table.cellrenderer.Abstract",
y='" style="',
z="abstract",
A="none",
B="hidden",
C="} ",
D='px;',
E=".qooxdoo-table-cell-italic { font-style:italic} ",
F="absolute";
qx.Class.define(x,
{type:z,
implement:qx.ui.table.ICellRenderer,
extend:qx.core.Object,
construct:function(){arguments.callee.base.call(this);
var G=qx.ui.table.cellrenderer.Abstract;
if(!G.__jJ){var H=qx.theme.manager.Color.getInstance();
G.__jJ=arguments.callee.self;
var I=c+
qx.bom.element.Style.compile({position:F,
top:n,
overflow:B,
whiteSpace:f,
borderRight:p+H.resolve(r),
padding:l,
cursor:g,
textOverflow:t,
userSelect:A})+C+k+E+q;
if(!qx.core.Variant.isSet(h,
s)){I+=c+qx.bom.element.BoxSizing.compile(v)+i;
}G.__jJ.stylesheet=qx.bom.Stylesheet.createElement(I);
}},
members:{_insetX:6+6+1,
_insetY:0,
_getCellClass:function(J){return d;
},
_getCellStyle:function(J){return J.style||a;
},
_getContentHtml:function(J){return J.value||a;
},
_getCellSizeStyle:function(K,
L,
M,
N){var O=a;
if(qx.bom.client.Feature.CONTENT_BOX){K-=M;
L-=N;
}O+=j+K+b;
O+=o+L+b;
return O;
},
createDataCellHtml:function(J,
P){P.push(m,
this._getCellClass(J),
y,
w,
J.styleLeft,
D,
this._getCellSizeStyle(J.styleWidth,
J.styleHeight,
this._insetX,
this._insetY),
this._getCellStyle(J),
u+this._getContentHtml(J),
e);
}}});
})();
(function(){var a="",
b="number",
c="Boolean",
d="qx.ui.table.cellrenderer.Default",
e=" qooxdoo-table-cell-bold",
f=" qooxdoo-table-cell-right",
g=" qooxdoo-table-cell-italic",
h="string";
qx.Class.define(d,
{extend:qx.ui.table.cellrenderer.Abstract,
statics:{STYLEFLAG_ALIGN_RIGHT:1,
STYLEFLAG_BOLD:2,
STYLEFLAG_ITALIC:4},
properties:{useAutoAlign:{check:c,
init:true}},
members:{_getStyleFlags:function(i){if(this.getUseAutoAlign()){if(typeof i.value==b){return qx.ui.table.cellrenderer.Default.STYLEFLAG_ALIGN_RIGHT;
}}},
_getCellClass:function(i){var j=arguments.callee.base.call(this,
i);
if(!j){return a;
}var k=this._getStyleFlags(i);
if(k&qx.ui.table.cellrenderer.Default.STYLEFLAG_ALIGN_RIGHT){j+=f;
}
if(k&qx.ui.table.cellrenderer.Default.STYLEFLAG_BOLD){j+=e;
}
if(k&qx.ui.table.cellrenderer.Default.STYLEFLAG_ITALIC){j+=g;
}return j;
},
_getContentHtml:function(i){return qx.bom.String.escape(this._formatValue(i));
},
_formatValue:function(i){var l=i.value;
if(l==null){return a;
}
if(typeof l==h){return l;
}else if(typeof l==b){if(!qx.ui.table.cellrenderer.Default._numberFormat){qx.ui.table.cellrenderer.Default._numberFormat=new qx.util.format.NumberFormat();
qx.ui.table.cellrenderer.Default._numberFormat.setMaximumFractionDigits(2);
}var m=qx.ui.table.cellrenderer.Default._numberFormat.format(l);
}else if(l instanceof Date){m=qx.util.format.DateFormat.getDateInstance().format(l);
}else{m=l;
}return m;
}}});
})();
(function(){var a="qx.ui.table.ICellEditorFactory";
qx.Interface.define(a,
{members:{createCellEditor:function(b){return true;
},
getCellEditorValue:function(c){return true;
}}});
})();
(function(){var a="",
b="Function",
c="number",
d="qx.ui.table.celleditor.TextField",
e="table-editor-textfield",
f="appear";
qx.Class.define(d,
{extend:qx.core.Object,
implement:qx.ui.table.ICellEditorFactory,
construct:function(){arguments.callee.base.call(this);
},
properties:{validationFunction:{check:b,
nullable:true,
init:null}},
members:{__jK:null,
createCellEditor:function(g){var h=new qx.ui.form.TextField;
h.setAppearance(e);
h.originalValue=g.value;
if(g.value===null){g.value=a;
}h.setValue(a+g.value);
h.addListener(f,
function(){h.selectAll();
});
return h;
},
getCellEditorValue:function(h){var i=h.getValue();
var j=this.getValidationFunction();
if(!this.__jK&&j){i=j(i,
h.originalValue);
this.__jK=true;
}
if(typeof h.originalValue==c){i=parseFloat(i);
}return i;
}}});
})();
(function(){var a="qx.event.type.DataEvent",
b="__jN",
c="visibilityChanged",
d="orderChanged",
e="__jM",
f="widthChanged",
g="qx.ui.table.columnmodel.Basic",
h="__jP",
j="__jO",
k="__jR",
l="__jS",
m="__jQ",
n="visibilityChangedPre";
qx.Class.define(g,
{extend:qx.core.Object,
events:{"widthChanged":a,
"visibilityChangedPre":a,
"visibilityChanged":a,
"orderChanged":a},
statics:{DEFAULT_WIDTH:100,
DEFAULT_HEADER_RENDERER:qx.ui.table.headerrenderer.Default,
DEFAULT_DATA_RENDERER:qx.ui.table.cellrenderer.Default,
DEFAULT_EDITOR_FACTORY:qx.ui.table.celleditor.TextField},
members:{__jL:null,
__jM:null,
__jN:null,
__jO:null,
__jP:null,
__jQ:null,
__jR:null,
__jS:null,
init:function(o){this.__jP=[];
var p=qx.ui.table.columnmodel.Basic.DEFAULT_WIDTH;
var q=this.__jQ=new qx.ui.table.columnmodel.Basic.DEFAULT_HEADER_RENDERER();
var r=this.__jR=new qx.ui.table.columnmodel.Basic.DEFAULT_DATA_RENDERER();
var s=this.__jS=new qx.ui.table.columnmodel.Basic.DEFAULT_EDITOR_FACTORY();
this.__jO=[];
this.__jN=[];
for(var t=0;t<o;t++){this.__jP[t]={width:p,
headerRenderer:q,
dataRenderer:r,
editorFactory:s};
this.__jO[t]=t;
this.__jN[t]=t;
}this.__jM=null;
},
getVisibleColumns:function(){return this.__jN;
},
setColumnWidth:function(t,
p){var u=this.__jP[t].width;
if(u!=p){this.__jP[t].width=p;
var v={col:t,
newWidth:p,
oldWidth:u};
this.fireDataEvent(f,
v);
}},
getColumnWidth:function(t){return this.__jP[t].width;
},
setHeaderCellRenderer:function(t,
w){var y=this.__jP[t].headerRenderer;
if(y!==this.__jQ){y.dispose();
}this.__jP[t].headerRenderer=w;
},
getHeaderCellRenderer:function(t){return this.__jP[t].headerRenderer;
},
setDataCellRenderer:function(t,
w){var y=this.__jP[t].headerRenderer;
if(y!==this.__jR){y.dispose();
}this.__jP[t].dataRenderer=w;
},
getDataCellRenderer:function(t){return this.__jP[t].dataRenderer;
},
setCellEditorFactory:function(t,
z){var y=this.__jP[t].headerRenderer;
if(y!==this.__jS){y.dispose();
}this.__jP[t].editorFactory=z;
},
getCellEditorFactory:function(t){return this.__jP[t].editorFactory;
},
_getColToXPosMap:function(){if(this.__jM==null){this.__jM={};
for(var A=0;A<this.__jO.length;A++){var t=this.__jO[A];
this.__jM[t]={overX:A};
}
for(var B=0;B<this.__jN.length;B++){var t=this.__jN[B];
this.__jM[t].visX=B;
}}return this.__jM;
},
getVisibleColumnCount:function(){return this.__jN.length;
},
getVisibleColumnAtX:function(C){return this.__jN[C];
},
getVisibleX:function(t){return this._getColToXPosMap()[t].visX;
},
getOverallColumnCount:function(){return this.__jO.length;
},
getOverallColumnAtX:function(D){return this.__jO[D];
},
getOverallX:function(t){return this._getColToXPosMap()[t].overX;
},
isColumnVisible:function(t){return (this._getColToXPosMap()[t].visX!=null);
},
setColumnVisible:function(t,
E){if(E!=this.isColumnVisible(t)){if(E){var F=this._getColToXPosMap();
var A=F[t].overX;
if(A==null){throw new Error("Showing column failed: "+t+". The column is not added to this TablePaneModel.");
}var G;
for(var H=A+1;H<this.__jO.length;H++){var I=this.__jO[H];
var J=F[I].visX;
if(J!=null){G=J;
break;
}}if(G==null){G=this.__jN.length;
}this.__jN.splice(G,
0,
t);
}else{var B=this.getVisibleX(t);
this.__jN.splice(B,
1);
}this.__jM=null;
if(!this.__jL){var v={col:t,
visible:E};
this.fireDataEvent(n,
v);
this.fireDataEvent(c,
v);
}}},
moveColumn:function(K,
L){this.__jL=true;
var t=this.__jO[K];
var E=this.isColumnVisible(t);
if(E){this.setColumnVisible(t,
false);
}this.__jO.splice(K,
1);
this.__jO.splice(L,
0,
t);
this.__jM=null;
if(E){this.setColumnVisible(t,
true);
}this.__jL=false;
var v={col:t,
fromOverXPos:K,
toOverXPos:L};
this.fireDataEvent(d,
v);
}},
destruct:function(){for(var M=0;M<this.__jP.length;M++){this.__jP[M].headerRenderer.dispose();
this.__jP[M].dataRenderer.dispose();
this.__jP[M].editorFactory.dispose();
}this._disposeFields(j,
b,
h,
e);
this._disposeObjects(m,
k,
l);
}});
})();
(function(){var a="icon",
b="label",
c="String",
d="sort-icon",
e="_applySortIcon",
f="_applyIcon",
g="table-header-cell",
h="qx.ui.table.headerrenderer.HeaderCell",
i="_applyLabel";
qx.Class.define(h,
{extend:qx.ui.container.Composite,
construct:function(){arguments.callee.base.call(this);
var j=new qx.ui.layout.Grid();
j.setRowFlex(0,
1);
j.setColumnFlex(1,
1);
j.setColumnFlex(2,
1);
this.setLayout(j);
},
properties:{appearance:{refine:true,
init:g},
label:{check:c,
init:null,
nullable:true,
apply:i},
sortIcon:{check:c,
init:null,
nullable:true,
apply:e,
themeable:true},
icon:{check:c,
init:null,
nullable:true,
apply:f}},
members:{_applyLabel:function(k,
l){if(k){this._showChildControl(b).setContent(k);
}else{this._excludeChildControl(b);
}},
_applySortIcon:function(k,
l){if(k){this._showChildControl(d).setSource(k);
}else{this._excludeChildControl(d);
}},
_applyIcon:function(k,
l){if(k){this._showChildControl(a).setSource(k);
}else{this._excludeChildControl(a);
}},
_createChildControlImpl:function(m){var n;
switch(m){case b:n=new qx.ui.basic.Label(this.getLabel()).set({anonymous:true,
allowShrinkX:true});
this._add(n,
{row:0,
column:1});
break;
case d:n=new qx.ui.basic.Image(this.getSortIcon());
n.setAnonymous(true);
this._add(n,
{row:0,
column:2});
break;
case a:n=new qx.ui.basic.Image(this.getIcon()).set({anonymous:true,
allowShrinkX:true});
this._add(n,
{row:0,
column:0});
break;
}return n||arguments.callee.base.call(this,
m);
}}});
})();
(function(){var a="\n",
b="",
c=" &nbsp;",
d="<br>",
e=" ",
f="qx.bom.String";
qx.Class.define(f,
{statics:{TO_CHARCODE:{"quot":34,
"amp":38,
"lt":60,
"gt":62,
"nbsp":160,
"iexcl":161,
"cent":162,
"pound":163,
"curren":164,
"yen":165,
"brvbar":166,
"sect":167,
"uml":168,
"copy":169,
"ordf":170,
"laquo":171,
"not":172,
"shy":173,
"reg":174,
"macr":175,
"deg":176,
"plusmn":177,
"sup2":178,
"sup3":179,
"acute":180,
"micro":181,
"para":182,
"middot":183,
"cedil":184,
"sup1":185,
"ordm":186,
"raquo":187,
"frac14":188,
"frac12":189,
"frac34":190,
"iquest":191,
"Agrave":192,
"Aacute":193,
"Acirc":194,
"Atilde":195,
"Auml":196,
"Aring":197,
"AElig":198,
"Ccedil":199,
"Egrave":200,
"Eacute":201,
"Ecirc":202,
"Euml":203,
"Igrave":204,
"Iacute":205,
"Icirc":206,
"Iuml":207,
"ETH":208,
"Ntilde":209,
"Ograve":210,
"Oacute":211,
"Ocirc":212,
"Otilde":213,
"Ouml":214,
"times":215,
"Oslash":216,
"Ugrave":217,
"Uacute":218,
"Ucirc":219,
"Uuml":220,
"Yacute":221,
"THORN":222,
"szlig":223,
"agrave":224,
"aacute":225,
"acirc":226,
"atilde":227,
"auml":228,
"aring":229,
"aelig":230,
"ccedil":231,
"egrave":232,
"eacute":233,
"ecirc":234,
"euml":235,
"igrave":236,
"iacute":237,
"icirc":238,
"iuml":239,
"eth":240,
"ntilde":241,
"ograve":242,
"oacute":243,
"ocirc":244,
"otilde":245,
"ouml":246,
"divide":247,
"oslash":248,
"ugrave":249,
"uacute":250,
"ucirc":251,
"uuml":252,
"yacute":253,
"thorn":254,
"yuml":255,
"fnof":402,
"Alpha":913,
"Beta":914,
"Gamma":915,
"Delta":916,
"Epsilon":917,
"Zeta":918,
"Eta":919,
"Theta":920,
"Iota":921,
"Kappa":922,
"Lambda":923,
"Mu":924,
"Nu":925,
"Xi":926,
"Omicron":927,
"Pi":928,
"Rho":929,
"Sigma":931,
"Tau":932,
"Upsilon":933,
"Phi":934,
"Chi":935,
"Psi":936,
"Omega":937,
"alpha":945,
"beta":946,
"gamma":947,
"delta":948,
"epsilon":949,
"zeta":950,
"eta":951,
"theta":952,
"iota":953,
"kappa":954,
"lambda":955,
"mu":956,
"nu":957,
"xi":958,
"omicron":959,
"pi":960,
"rho":961,
"sigmaf":962,
"sigma":963,
"tau":964,
"upsilon":965,
"phi":966,
"chi":967,
"psi":968,
"omega":969,
"thetasym":977,
"upsih":978,
"piv":982,
"bull":8226,
"hellip":8230,
"prime":8242,
"Prime":8243,
"oline":8254,
"frasl":8260,
"weierp":8472,
"image":8465,
"real":8476,
"trade":8482,
"alefsym":8501,
"larr":8592,
"uarr":8593,
"rarr":8594,
"darr":8595,
"harr":8596,
"crarr":8629,
"lArr":8656,
"uArr":8657,
"rArr":8658,
"dArr":8659,
"hArr":8660,
"forall":8704,
"part":8706,
"exist":8707,
"empty":8709,
"nabla":8711,
"isin":8712,
"notin":8713,
"ni":8715,
"prod":8719,
"sum":8721,
"minus":8722,
"lowast":8727,
"radic":8730,
"prop":8733,
"infin":8734,
"ang":8736,
"and":8743,
"or":8744,
"cap":8745,
"cup":8746,
"int":8747,
"there4":8756,
"sim":8764,
"cong":8773,
"asymp":8776,
"ne":8800,
"equiv":8801,
"le":8804,
"ge":8805,
"sub":8834,
"sup":8835,
"sube":8838,
"supe":8839,
"oplus":8853,
"otimes":8855,
"perp":8869,
"sdot":8901,
"lceil":8968,
"rceil":8969,
"lfloor":8970,
"rfloor":8971,
"lang":9001,
"rang":9002,
"loz":9674,
"spades":9824,
"clubs":9827,
"hearts":9829,
"diams":9830,
"OElig":338,
"oelig":339,
"Scaron":352,
"scaron":353,
"Yuml":376,
"circ":710,
"tilde":732,
"ensp":8194,
"emsp":8195,
"thinsp":8201,
"zwnj":8204,
"zwj":8205,
"lrm":8206,
"rlm":8207,
"ndash":8211,
"mdash":8212,
"lsquo":8216,
"rsquo":8217,
"sbquo":8218,
"ldquo":8220,
"rdquo":8221,
"bdquo":8222,
"dagger":8224,
"Dagger":8225,
"permil":8240,
"lsaquo":8249,
"rsaquo":8250,
"euro":8364},
escape:function(g){return qx.util.StringEscape.escape(g,
qx.bom.String.FROM_CHARCODE);
},
unescape:function(g){return qx.util.StringEscape.unescape(g,
qx.bom.String.TO_CHARCODE);
},
fromText:function(g){return qx.bom.String.escape(g).replace(/(  |\n)/g,
function(h){var i={"  ":c,
"\n":d};
return i[h]||h;
});
},
toText:function(g){return qx.bom.String.unescape(g.replace(/\s+|<([^>])+>/gi,
function(h){if(/\s+/.test(h)){return e;
}else if(/^<BR|^<br/gi.test(h)){return a;
}else{return b;
}}));
}},
defer:function(j,
k,
l){j.FROM_CHARCODE=qx.lang.Object.invert(j.TO_CHARCODE);
}});
})();
(function(){var a=";",
b="&",
c="",
d="&#",
e='X',
f='#',
g="qx.client",
h="qx.util.StringEscape";
qx.Bootstrap.define(h,
{statics:{escape:qx.core.Variant.select(g,
{"mshtml":function(j,
k){var m,
n=[];
for(var o=0,
p=j.length;o<p;o++){var q=j.charAt(o);
var r=q.charCodeAt(0);
if(k[r]){m=b+k[r]+a;
}else{if(r>0x7F){m=d+r+a;
}else{m=q;
}}n[n.length]=m;
}return n.join(c);
},
"default":function(j,
k){var m,
n=c;
for(var o=0,
p=j.length;o<p;o++){var q=j.charAt(o);
var r=q.charCodeAt(0);
if(k[r]){m=b+k[r]+a;
}else{if(r>0x7F){m=d+r+a;
}else{m=q;
}}n+=m;
}return n;
}}),
unescape:function(j,
s){return j.replace(/&[#\w]+;/gi,
function(m){var q=m;
var m=m.substring(1,
m.length-1);
var r=s[m];
if(r){q=String.fromCharCode(r);
}else{if(m.charAt(0)==f){if(m.charAt(1).toUpperCase()==e){r=m.substring(2);
if(r.match(/^[0-9A-Fa-f]+$/gi)){q=String.fromCharCode(parseInt(r,
16));
}}else{r=m.substring(1);
if(r.match(/^\d+$/gi)){q=String.fromCharCode(parseInt(r,
10));
}}}}return q;
});
}}});
})();
(function(){var a="qx.util.format.IFormat";
qx.Interface.define(a,
{members:{format:function(b){},
parse:function(c){}}});
})();
(function(){var a="",
b="Number",
c="-",
d="0",
e="String",
f='(',
g="g",
h="Boolean",
i="$",
j="NaN",
k='([0-9]{1,3}(?:',
l='{0,1}[0-9]{3}){0,})',
m="qx.util.format.NumberFormat",
n='\\d+){0,1}',
o="^",
p=".",
q="-Infinity",
r="Infinity",
s='([-+]){0,1}';
qx.Class.define(m,
{extend:qx.core.Object,
implement:qx.util.format.IFormat,
construct:function(t){arguments.callee.base.call(this);
this._locale=t;
},
statics:{getIntegerInstance:function(){var u=qx.util.format.NumberFormat;
if(u._integerInstance==null){u._integerInstance=new u();
u._integerInstance.setMaximumFractionDigits(0);
}return u._integerInstance;
},
getInstance:function(){if(!this._instance){this._instance=new this;
}return this._instance;
}},
properties:{minimumIntegerDigits:{check:b,
init:0},
maximumIntegerDigits:{check:b,
nullable:true},
minimumFractionDigits:{check:b,
init:0},
maximumFractionDigits:{check:b,
nullable:true},
groupingUsed:{check:h,
init:true},
prefix:{check:e,
init:a},
postfix:{check:e,
init:a}},
members:{format:function(v){switch(v){case Infinity:return r;
case -Infinity:return q;
case NaN:return j;
}var w=(v<0);
if(w){v=-v;
}
if(this.getMaximumFractionDigits()!=null){var x=Math.pow(10,
this.getMaximumFractionDigits());
v=Math.round(v*x)/x;
}var y=String(Math.floor(v)).length;
var z=a+v;
var A=z.substring(0,
y);
while(A.length<this.getMinimumIntegerDigits()){A=d+A;
}
if(this.getMaximumIntegerDigits()!=null&&A.length>this.getMaximumIntegerDigits()){A=A.substring(A.length-this.getMaximumIntegerDigits());
}var B=z.substring(y+1);
while(B.length<this.getMinimumFractionDigits()){B+=d;
}
if(this.getMaximumFractionDigits()!=null&&B.length>this.getMaximumFractionDigits()){B=B.substring(0,
this.getMaximumFractionDigits());
}if(this.getGroupingUsed()){var C=A;
A=a;
var D;
for(D=C.length;D>3;D-=3){A=a+qx.locale.Number.getGroupSeparator(this._locale)+C.substring(D-3,
D)+A;
}A=C.substring(0,
D)+A;
}var E=this.getPrefix()?this.getPrefix():a;
var F=this.getPostfix()?this.getPostfix():a;
var G=E+(w?c:a)+A;
if(B.length>0){G+=a+qx.locale.Number.getDecimalSeparator(this._locale)+B;
}G+=F;
return G;
},
parse:function(G){var H=qx.lang.String.escapeRegexpChars(qx.locale.Number.getGroupSeparator(this._locale)+a);
var I=qx.lang.String.escapeRegexpChars(qx.locale.Number.getDecimalSeparator(this._locale)+a);
var J=new RegExp(o+qx.lang.String.escapeRegexpChars(this.getPrefix())+s+k+H+l+f+I+n+qx.lang.String.escapeRegexpChars(this.getPostfix())+i);
var K=J.exec(G);
if(K==null){throw new Error("Number string '"+G+"' does not match the number format");
}var w=(K[1]==c);
var A=K[2];
var B=K[3];
A=A.replace(new RegExp(H,
g),
a);
var L=(w?c:a)+A;
if(B!=null&&B.length!=0){B=B.replace(new RegExp(I),
a);
L+=p+B;
}return parseFloat(L);
}}});
})();
(function(){var a="cldr_number_decimal_separator",
b="cldr_number_percent_format",
c="qx.locale.Number",
d="cldr_number_group_separator";
qx.Class.define(c,
{statics:{getDecimalSeparator:function(e){return qx.locale.Manager.getInstance().localize(a,
[],
e);
},
getGroupSeparator:function(e){return qx.locale.Manager.getInstance().localize(d,
[],
e);
},
getPercentFormat:function(e){return qx.locale.Manager.getInstance().localize(b,
[],
e);
}}});
})();
(function(){var a="(\\d\\d?)",
b="",
c="(",
d=")",
e="|",
f="abbreviated",
g="wide",
h="wildcard",
j="default",
k="literal",
l="'",
m="hour",
n="(\\d\\d?\\d?)",
o="ms",
p="-",
q="quoted_literal",
r='a',
s="+",
t="HHmmss",
u="long",
v="HH:mm:ss",
w='z',
x="sec",
y="day",
z="narrow",
A='Z',
B="min",
C=" ",
D="SSS",
E="__jT",
F="h",
G="SS",
H="Z",
I="00",
J='K',
K="EEEE",
L="^",
M='y',
N="__jU",
O="(\\d\\d(\\d\\d)?)",
P="(\\d\\d)",
Q="K",
R="((\\+|\\-)\\d\\d:?\\d\\d)",
S="a",
T="GMT",
U="S",
V="dd",
W="qx.util.format.DateFormat",
X="__kd",
Y="H",
ba="HH",
bb="EE",
bc="mm",
bd='h',
be='s',
bf='A',
bg="KK",
bh="ss",
bi='H',
bj='S',
bk="0",
bl="MMMM",
bm="d",
bn="([a-zA-Z]+)",
bo='k',
bp="m",
bq='D',
br="kk",
bs="hh",
bt="MM",
bu="yy",
bv="yyyy-MM-dd HH:mm:ss",
bw="short",
bx='d',
by="unkown",
bz='m',
bA=":00",
bB="__jV",
bC="k",
bD='M',
bE="MMM",
bF="s",
bG="M",
bH='w',
bI="EEE",
bJ="$",
bK="?",
bL='E',
bM="z",
bN="yyyy",
bO="__jW";
qx.Class.define(W,
{extend:qx.core.Object,
implement:qx.util.format.IFormat,
construct:function(bP,
bQ){arguments.callee.base.call(this);
if(!bQ){this.__jT=qx.locale.Manager.getInstance().getLocale();
}else{this.__jT=bQ;
}
if(bP!=null){this.__jU=bP.toString();
}else{this.__jU=qx.locale.Date.getDateFormat(u,
this.__jT)+C+qx.locale.Date.getDateTimeFormat(t,
v,
this.__jT);
}},
statics:{getDateTimeInstance:function(){var bR=qx.util.format.DateFormat;
var bP=qx.locale.Date.getDateFormat(u)+C+qx.locale.Date.getDateTimeFormat(t,
v);
if(bR._dateInstance==null||bR.__jU!=bP){bR._dateTimeInstance=new bR();
}return bR._dateTimeInstance;
},
getDateInstance:function(){var bR=qx.util.format.DateFormat;
var bP=qx.locale.Date.getDateFormat(bw)+b;
if(bR._dateInstance==null||bR.__jU!=bP){bR._dateInstance=new bR(bP);
}return bR._dateInstance;
},
ASSUME_YEAR_2000_THRESHOLD:30,
LOGGING_DATE_TIME__format:bv,
AM_MARKER:"am",
PM_MARKER:"pm",
MEDIUM_TIMEZONE_NAMES:["GMT"],
FULL_TIMEZONE_NAMES:["Greenwich Mean Time"]},
members:{__jT:null,
__jU:null,
__jV:null,
__jW:null,
__jX:function(bS,
bT){var bU=b+bS;
while(bU.length<bT){bU=bk+bU;
}return bU;
},
__jY:function(bV){var bW=new Date(bV.getTime());
var bX=bW.getDate();
while(bW.getMonth()!=0){bW.setDate(-1);
bX+=bW.getDate()+1;
}return bX;
},
__ka:function(bV){return new Date(bV.getTime()+(3-((bV.getDay()+6)%7))*86400000);
},
__kb:function(bV){var bY=this.__ka(bV);
var ca=bY.getFullYear();
var cb=this.__ka(new Date(ca,
0,
4));
return Math.floor(1.5+(bY.getTime()-cb.getTime())/86400000/7);
},
format:function(bV){var bR=qx.util.format.DateFormat;
var bQ=this.__jT;
var cc=bV.getFullYear();
var cd=bV.getMonth();
var ce=bV.getDate();
var cf=bV.getDay();
var cg=bV.getHours();
var ch=bV.getMinutes();
var ci=bV.getSeconds();
var cj=bV.getMilliseconds();
var ck=bV.getTimezoneOffset()/60;
this.__kc();
var cl=b;
for(var cm=0;cm<this.__kd.length;cm++){var cn=this.__kd[cm];
if(cn.type==k){cl+=cn.text;
}else{var co=cn.character;
var cp=cn.size;
var cq=bK;
switch(co){case M:if(cp==2){cq=this.__jX(cc%100,
2);
}else if(cp==4){cq=cc;
}break;
case bq:cq=this.__jX(this.__jY(bV),
cp);
break;
case bx:cq=this.__jX(ce,
cp);
break;
case bH:cq=this.__jX(this.__kb(bV),
cp);
break;
case bL:if(cp==2){cq=qx.locale.Date.getDayName(z,
cf,
bQ);
}else if(cp==3){cq=qx.locale.Date.getDayName(f,
cf,
bQ);
}else if(cp==4){cq=qx.locale.Date.getDayName(g,
cf,
bQ);
}break;
case bD:if(cp==1||cp==2){cq=this.__jX(cd+1,
cp);
}else if(cp==3){cq=qx.locale.Date.getMonthName(f,
cd,
bQ);
}else if(cp==4){cq=qx.locale.Date.getMonthName(g,
cd,
bQ);
}break;
case r:cq=(cg<12)?qx.locale.Date.getAmMarker(bQ):qx.locale.Date.getPmMarker(bQ);
break;
case bi:cq=this.__jX(cg,
cp);
break;
case bo:cq=this.__jX((cg==0)?24:cg,
cp);
break;
case J:cq=this.__jX(cg%12,
cp);
break;
case bd:cq=this.__jX(((cg%12)==0)?12:(cg%12),
cp);
break;
case bz:cq=this.__jX(ch,
cp);
break;
case be:cq=this.__jX(ci,
cp);
break;
case bj:cq=this.__jX(cj,
cp);
break;
case w:if(cp==1){cq=T+((ck<0)?p:s)+this.__jX(ck)+bA;
}else if(cp==2){cq=bR.MEDIUM_TIMEZONE_NAMES[ck];
}else if(cp==3){cq=bR.FULL_TIMEZONE_NAMES[ck];
}break;
case A:cq=((ck<0)?p:s)+this.__jX(ck,
2)+I;
}cl+=cq;
}}return cl;
},
parse:function(cr){this.__ke();
var cs=this.__jV.regex.exec(cr);
if(cs==null){throw new Error("Date string '"+cr+"' does not match the date format: "+this.__jU);
}var ct={year:1970,
month:0,
day:1,
hour:0,
ispm:false,
min:0,
sec:0,
ms:0};
var cu=1;
for(var cm=0;cm<this.__jV.usedRules.length;cm++){var cv=this.__jV.usedRules[cm];
var cw=cs[cu];
if(cv.field!=null){ct[cv.field]=parseInt(cw,
10);
}else{cv.manipulator(ct,
cw);
}cu+=(cv.groups==null)?1:cv.groups;
}var bV=new Date(ct.year,
ct.month,
ct.day,
(ct.ispm)?(ct.hour+12):ct.hour,
ct.min,
ct.sec,
ct.ms);
if(ct.month!=bV.getMonth()||ct.year!=bV.getFullYear()){throw new Error("Error parsing date '"+cr+"': the value for day or month is too large");
}return bV;
},
__kc:function(){if(this.__kd!=null){return;
}this.__kd=[];
var cx;
var cy=0;
var cz=b;
var bP=this.__jU;
var cA=j;
var cm=0;
while(cm<bP.length){var cB=bP.charAt(cm);
switch(cA){case q:if(cB==l){if(cm+1>=bP.length){cm++;
break;
}var cC=bP.charAt(cm+1);
if(cC==l){cz+=cB;
cm++;
}else{cm++;
cA=by;
}}else{cz+=cB;
cm++;
}break;
case h:if(cB==cx){cy++;
cm++;
}else{this.__kd.push({type:h,
character:cx,
size:cy});
cx=null;
cy=0;
cA=j;
}break;
default:if((cB>=r&&cB<=w)||(cB>=bf&&cB<=A)){cx=cB;
cA=h;
}else if(cB==l){if(cm+1>=bP.length){cz+=cB;
cm++;
break;
}var cC=bP.charAt(cm+1);
if(cC==l){cz+=cB;
cm++;
}cm++;
cA=q;
}else{cA=j;
}
if(cA!=j){if(cz.length>0){this.__kd.push({type:k,
text:cz});
cz=b;
}}else{cz+=cB;
cm++;
}break;
}}if(cx!=null){this.__kd.push({type:h,
character:cx,
size:cy});
}else if(cz.length>0){this.__kd.push({type:k,
text:cz});
}},
__ke:function(){if(this.__jV!=null){return ;
}var bP=this.__jU;
this.__kf();
this.__kc();
var cD=[];
var cE=L;
for(var cF=0;cF<this.__kd.length;cF++){var cn=this.__kd[cF];
if(cn.type==k){cE+=qx.lang.String.escapeRegexpChars(cn.text);
}else{var co=cn.character;
var cp=cn.size;
var cG;
for(var cH=0;cH<this.__jW.length;cH++){var cv=this.__jW[cH];
if(co==cv.pattern.charAt(0)&&cp==cv.pattern.length){cG=cv;
break;
}}if(cG==null){var cI=b;
for(var cm=0;cm<cp;cm++){cI+=co;
}throw new Error("Malformed date format: "+bP+". Wildcard "+cI+" is not supported");
}else{cD.push(cG);
cE+=cG.regex;
}}}cE+=bJ;
var cJ;
try{cJ=new RegExp(cE);
}catch(exc){throw new Error("Malformed date format: "+bP);
}this.__jV={regex:cJ,
"usedRules":cD,
pattern:cE};
},
__kf:function(){var bR=qx.util.format.DateFormat;
if(this.__jW!=null){return ;
}this.__jW=[];
var cK=function(ct,
cw){cw=parseInt(cw,
10);
if(cw<bR.ASSUME_YEAR_2000_THRESHOLD){cw+=2000;
}else if(cw<100){cw+=1900;
}ct.year=cw;
};
var cL=function(ct,
cw){ct.month=parseInt(cw,
10)-1;
};
var cM=function(ct,
cw){ct.ispm=(cw==bR.PM_MARKER);
};
var cN=function(ct,
cw){ct.hour=parseInt(cw,
10)%24;
};
var cO=function(ct,
cw){ct.hour=parseInt(cw,
10)%12;
};
var cP=function(ct,
cw){return;
};
var cQ=qx.locale.Date.getMonthNames(f,
this.__jT);
for(var cm=0;cm<cQ.length;cm++){cQ[cm]=qx.lang.String.escapeRegexpChars(cQ[cm].toString());
}var cR=function(ct,
cw){cw=qx.lang.String.escapeRegexpChars(cw);
ct.month=cQ.indexOf(cw);
};
var cS=qx.locale.Date.getMonthNames(g,
this.__jT);
for(var cm=0;cm<cS.length;cm++){cS[cm]=qx.lang.String.escapeRegexpChars(cS[cm].toString());
}var cT=function(ct,
cw){cw=qx.lang.String.escapeRegexpChars(cw);
ct.month=cS.indexOf(cw);
};
var cU=qx.locale.Date.getDayNames(z,
this.__jT);
for(var cm=0;cm<cU.length;cm++){cU[cm]=qx.lang.String.escapeRegexpChars(cU[cm].toString());
}var cV=function(ct,
cw){cw=qx.lang.String.escapeRegexpChars(cw);
ct.month=cU.indexOf(cw);
};
var cW=qx.locale.Date.getDayNames(f,
this.__jT);
for(var cm=0;cm<cW.length;cm++){cW[cm]=qx.lang.String.escapeRegexpChars(cW[cm].toString());
}var cX=function(ct,
cw){cw=qx.lang.String.escapeRegexpChars(cw);
ct.month=cW.indexOf(cw);
};
var cY=qx.locale.Date.getDayNames(g,
this.__jT);
for(var cm=0;cm<cY.length;cm++){cY[cm]=qx.lang.String.escapeRegexpChars(cY[cm].toString());
}var da=function(ct,
cw){cw=qx.lang.String.escapeRegexpChars(cw);
ct.month=cY.indexOf(cw);
};
this.__jW.push({pattern:bN,
regex:O,
groups:2,
manipulator:cK});
this.__jW.push({pattern:bu,
regex:P,
manipulator:cK});
this.__jW.push({pattern:bG,
regex:a,
manipulator:cL});
this.__jW.push({pattern:bt,
regex:a,
manipulator:cL});
this.__jW.push({pattern:bE,
regex:c+cQ.join(e)+d,
manipulator:cR});
this.__jW.push({pattern:bl,
regex:c+cS.join(e)+d,
manipulator:cT});
this.__jW.push({pattern:V,
regex:a,
field:y});
this.__jW.push({pattern:bm,
regex:a,
field:y});
this.__jW.push({pattern:bb,
regex:c+cU.join(e)+d,
manipulator:cV});
this.__jW.push({pattern:bI,
regex:c+cW.join(e)+d,
manipulator:cX});
this.__jW.push({pattern:K,
regex:c+cY.join(e)+d,
manipulator:da});
this.__jW.push({pattern:S,
regex:c+bR.AM_MARKER+e+bR.PM_MARKER+d,
manipulator:cM});
this.__jW.push({pattern:ba,
regex:a,
field:m});
this.__jW.push({pattern:Y,
regex:a,
field:m});
this.__jW.push({pattern:br,
regex:a,
manipulator:cN});
this.__jW.push({pattern:bC,
regex:a,
manipulator:cN});
this.__jW.push({pattern:bg,
regex:a,
field:m});
this.__jW.push({pattern:Q,
regex:a,
field:m});
this.__jW.push({pattern:bs,
regex:a,
manipulator:cO});
this.__jW.push({pattern:F,
regex:a,
manipulator:cO});
this.__jW.push({pattern:bc,
regex:a,
field:B});
this.__jW.push({pattern:bp,
regex:a,
field:B});
this.__jW.push({pattern:bh,
regex:a,
field:x});
this.__jW.push({pattern:bF,
regex:a,
field:x});
this.__jW.push({pattern:D,
regex:n,
field:o});
this.__jW.push({pattern:G,
regex:n,
field:o});
this.__jW.push({pattern:U,
regex:n,
field:o});
this.__jW.push({pattern:H,
regex:R,
manipulator:cP});
this.__jW.push({pattern:bM,
regex:bn,
manipulator:cP});
}},
destruct:function(){this._disposeFields(N,
E,
X,
bB,
bO);
}});
})();
(function(){var a="_",
b="thu",
c="sat",
d="cldr_day_",
e="cldr_month_",
f="wed",
g="fri",
h="tue",
j="mon",
k="sun",
l="short",
m="HH:mm",
n="HHmmsszz",
o="HHmm",
p="HHmmss",
q="cldr_date_format_",
r="HH:mm:ss zz",
s="full",
t="cldr_pm",
u="long",
v="medium",
w="cldr_am",
x="qx.locale.Date",
y="cldr_date_time_format_",
z="cldr_time_format_",
A="HH:mm:ss";
qx.Class.define(x,
{statics:{__kg:qx.locale.Manager.getInstance(),
getAmMarker:function(B){return this.__kg.localize(w,
[],
B);
},
getPmMarker:function(B){return this.__kg.localize(t,
[],
B);
},
getDayNames:function(C,
B){{};
var D=[k,
j,
h,
f,
b,
g,
c];
var E=[];
for(var F=0;F<D.length;F++){var G=d+C+a+D[F];
E.push(this.__kg.localize(G,
[],
B));
}return E;
},
getDayName:function(C,
H,
B){{};
var D=[k,
j,
h,
f,
b,
g,
c];
var G=d+C+a+D[H];
return this.__kg.localize(G,
[],
B);
},
getMonthNames:function(C,
B){{};
var E=[];
for(var F=0;F<12;F++){var G=e+C+a+(F+1);
E.push(this.__kg.localize(G,
[],
B));
}return E;
},
getMonthName:function(C,
I,
B){{};
var G=e+C+a+(I+1);
return this.__kg.localize(G,
[],
B);
},
getDateFormat:function(J,
B){{};
var G=q+J;
return this.__kg.localize(G,
[],
B);
},
getDateTimeFormat:function(K,
L,
B){var G=y+K;
var M=this.__kg.localize(G,
[],
B);
if(M==G){M=L;
}return M;
},
getTimeFormat:function(J,
B){{};
var G=z+J;
var M=this.__kg.localize(G,
[],
B);
if(M!=G){return M;
}
switch(J){case l:case v:return qx.locale.Date.getDateTimeFormat(o,
m);
case u:return qx.locale.Date.getDateTimeFormat(p,
A);
case s:return qx.locale.Date.getDateTimeFormat(n,
r);
default:throw new Error("This case should never happen.");
}},
getWeekStart:function(B){var N={"MV":5,
"AE":6,
"AF":6,
"BH":6,
"DJ":6,
"DZ":6,
"EG":6,
"ER":6,
"ET":6,
"IQ":6,
"IR":6,
"JO":6,
"KE":6,
"KW":6,
"LB":6,
"LY":6,
"MA":6,
"OM":6,
"QA":6,
"SA":6,
"SD":6,
"SO":6,
"TN":6,
"YE":6,
"AS":0,
"AU":0,
"AZ":0,
"BW":0,
"CA":0,
"CN":0,
"FO":0,
"GE":0,
"GL":0,
"GU":0,
"HK":0,
"IE":0,
"IL":0,
"IS":0,
"JM":0,
"JP":0,
"KG":0,
"KR":0,
"LA":0,
"MH":0,
"MN":0,
"MO":0,
"MP":0,
"MT":0,
"NZ":0,
"PH":0,
"PK":0,
"SG":0,
"TH":0,
"TT":0,
"TW":0,
"UM":0,
"US":0,
"UZ":0,
"VI":0,
"ZA":0,
"ZW":0,
"MW":0,
"NG":0,
"TJ":0};
var O=qx.locale.Date._getTerritory(B);
return N[O]!=null?N[O]:1;
},
getWeekendStart:function(B){var P={"EG":5,
"IL":5,
"SY":5,
"IN":0,
"AE":4,
"BH":4,
"DZ":4,
"IQ":4,
"JO":4,
"KW":4,
"LB":4,
"LY":4,
"MA":4,
"OM":4,
"QA":4,
"SA":4,
"SD":4,
"TN":4,
"YE":4};
var O=qx.locale.Date._getTerritory(B);
return P[O]!=null?P[O]:6;
},
getWeekendEnd:function(B){var Q={"AE":5,
"BH":5,
"DZ":5,
"IQ":5,
"JO":5,
"KW":5,
"LB":5,
"LY":5,
"MA":5,
"OM":5,
"QA":5,
"SA":5,
"SD":5,
"TN":5,
"YE":5,
"AF":5,
"IR":5,
"EG":6,
"IL":6,
"SY":6};
var O=qx.locale.Date._getTerritory(B);
return Q[O]!=null?Q[O]:0;
},
isWeekend:function(H,
B){var P=qx.locale.Date.getWeekendStart(B);
var Q=qx.locale.Date.getWeekendEnd(B);
if(Q>P){return ((H>=P)&&(H<=Q));
}else{return ((H>=P)||(H<=Q));
}},
_getTerritory:function(B){if(B){var O=B.split(a)[1]||B;
}else{O=this.__kg.getTerritory()||this.__kg.getLanguage();
}return O.toUpperCase();
}}});
})();
(function(){var a="none",
b="qx.client",
c="color",
d="qx.event.type.Data",
f="readonly",
g="changeValue",
h="readOnly",
i="text",
j="_applyTextAlign",
k="Boolean",
l="gecko",
m="A",
n="string",
o="change",
p="textAlign",
q="center",
r="disabled",
s="_applyReadOnly",
t="resize",
u="qx.ui.form.AbstractField",
v="transparent",
w="spellcheck",
x="false",
y="right",
z="abstract",
A="block",
B="changeName",
C="webkit",
D="String",
E="left";
qx.Class.define(u,
{extend:qx.ui.core.Widget,
implement:qx.ui.form.IFormElement,
type:z,
construct:function(F){arguments.callee.base.call(this);
if(F!=null){this.setValue(F);
}this.getContentElement().addListener(o,
this._onChangeContent,
this);
},
events:{"input":d,
"changeValue":d},
properties:{name:{check:D,
nullable:true,
event:B},
textAlign:{check:[E,
q,
y],
nullable:true,
themeable:true,
apply:j},
readOnly:{check:k,
apply:s,
init:false},
selectable:{refine:true,
init:true},
focusable:{refine:true,
init:true}},
members:{getFocusElement:function(){return this.getContentElement();
},
_createInputElement:function(){return new qx.html.Input(i);
},
_createContentElement:function(){var G=this._createInputElement();
if(qx.core.Variant.isSet(b,
l)){G.setAttribute(w,
x);
}G.setStyles({"border":a,
"padding":0,
"margin":0,
"display":A,
"background":v,
"outline":a,
"appearance":a});
if(qx.core.Variant.isSet(b,
C)){G.setStyle(t,
a);
}return G;
},
_applyEnabled:function(F,
H){arguments.callee.base.call(this,
F,
H);
this.getContentElement().setAttribute(r,
F===false);
},
_textSize:{width:16,
height:16},
_getContentHint:function(){return {width:this._textSize.width*10,
height:this._textSize.height||16};
},
_applyFont:function(F,
H){var I;
if(F){var J=qx.theme.manager.Font.getInstance().resolve(F);
I=J.getStyles();
}else{I=qx.bom.Font.getDefaultStyles();
}this.getContentElement().setStyles(I);
if(F){this._textSize=qx.bom.Label.getTextSize(m,
I);
}else{delete this._textSize;
}qx.ui.core.queue.Layout.add(this);
},
_applyTextColor:function(F,
H){if(F){this.getContentElement().setStyle(c,
qx.theme.manager.Color.getInstance().resolve(F));
}else{this.getContentElement().removeStyle(c);
}},
tabFocus:function(){arguments.callee.base.call(this);
this.selectAll();
},
setValue:function(F){if(typeof F===n||F instanceof String){var K=this.getContentElement();
if(K.getValue()!=F){K.setValue(F);
this.fireNonBubblingEvent(g,
qx.event.type.Data,
[F]);
}return F;
}throw new Error("Invalid value type: "+F);
},
getValue:function(){return this.getContentElement().getValue();
},
_onChangeContent:function(L){this.fireNonBubblingEvent(g,
qx.event.type.Data,
[L.getData()]);
},
getSelection:function(){return this.getContentElement().getSelection();
},
getSelectionLength:function(){return this.getContentElement().getSelectionLength();
},
setSelection:function(M,
N){this.getContentElement().setSelection(M,
N);
},
clearSelection:function(){this.getContentElement().clearSelection();
},
selectAll:function(){this.setSelection(0);
},
_applyTextAlign:function(F,
H){this.getContentElement().setStyle(p,
F);
},
_applyReadOnly:function(F,
H){this.getContentElement().setAttribute(h,
F);
if(F){this.addState(f);
}else{this.removeState(f);
}}}});
})();
(function(){var a="input",
b="text",
c="qx.ui.form.TextField",
d="",
f="_applyMaxLength",
g="textfield",
h="Integer",
i="maxLength",
j="qx.event.type.Data";
qx.Class.define(c,
{extend:qx.ui.form.AbstractField,
properties:{maxLength:{check:h,
apply:f,
nullable:true},
appearance:{refine:true,
init:g},
allowGrowY:{refine:true,
init:false},
allowShrinkY:{refine:true,
init:false}},
events:{"input":j},
members:{_createInputElement:function(){var k=new qx.html.Input(b);
k.addListener(a,
this._onHtmlInput,
this);
return k;
},
_onHtmlInput:function(l){this.fireDataEvent(a,
l.getData());
},
_applyMaxLength:function(m,
n){this.getContentElement().setAttribute(i,
m==null?d:m);
}}});
})();
(function(){var a="wrap",
b="value",
c="textarea",
d="",
e="input",
f="qx.html.Input",
g="select";
qx.Class.define(f,
{extend:qx.html.Element,
construct:function(h){arguments.callee.base.call(this);
this.__kh=h;
if(h===g||h===c){this._nodeName=h;
}else{this._nodeName=e;
}},
members:{_createDomElement:function(){return qx.bom.Input.create(this.__kh);
},
_applyProperty:function(i,
j){arguments.callee.base.call(this,
i,
j);
if(i===b){qx.bom.Input.setValue(this._element,
j);
}else if(i===a){qx.bom.Input.setWrap(this._element,
j);
}},
setValue:function(j){if(this._element){if(this._element.value!=j){qx.bom.Input.setValue(this._element,
j);
}}else{this._setProperty(b,
j);
}return this;
},
getValue:function(){if(this._element){return qx.bom.Input.getValue(this._element);
}return this._getProperty(b)||d;
},
setWrap:function(k){if(this.__kh===c){this._setProperty(a,
k);
}else{throw new Error("Text wrapping is only support by textareas!");
}return this;
},
getWrap:function(){if(this.__kh===c){return this._getProperty(a);
}else{throw new Error("Text wrapping is only support by textareas!");
}}}});
})();
(function(){var a="change",
b="input",
c="checkbox",
d="radio",
f="textarea",
g="text",
h="qx.client",
j="propertychange",
k="select-multiple",
m="checked",
n="value",
p="select",
q="qx.event.handler.Input";
qx.Class.define(q,
{extend:qx.core.Object,
implement:qx.event.IEventHandler,
construct:function(){arguments.callee.base.call(this);
this._onChangeCheckedWrapper=qx.lang.Function.listener(this._onChangeChecked,
this);
this._onChangeValueWrapper=qx.lang.Function.listener(this._onChangeValue,
this);
this._onInputWrapper=qx.lang.Function.listener(this._onInput,
this);
this._onPropertyWrapper=qx.lang.Function.listener(this._onProperty,
this);
},
statics:{PRIORITY:qx.event.Registration.PRIORITY_NORMAL,
SUPPORTED_TYPES:{input:1,
change:1},
TARGET_CHECK:qx.event.IEventHandler.TARGET_DOMNODE,
IGNORE_CAN_HANDLE:false},
members:{canHandleEvent:function(r,
s){var t=r.tagName.toLowerCase();
if(s===b&&(t===b||t===f)){return true;
}
if(s===a&&(t===b||t===f||t===p)){return true;
}return false;
},
registerEvent:qx.core.Variant.select(h,
{"mshtml":function(r,
s,
u){if(!r.__ki){var v=r.tagName.toLowerCase();
var s=r.type;
if(s===g||v===f||s===c||s===d){qx.bom.Event.addNativeListener(r,
j,
this._onPropertyWrapper);
}
if(s!==c&&s!==d){qx.bom.Event.addNativeListener(r,
a,
this._onChangeValueWrapper);
}r.__ki=true;
}},
"default":function(r,
s,
u){if(s===b){qx.bom.Event.addNativeListener(r,
b,
this._onInputWrapper);
}else if(s===a){if(r.type===d||r.type===c){qx.bom.Event.addNativeListener(r,
a,
this._onChangeCheckedWrapper);
}else{qx.bom.Event.addNativeListener(r,
a,
this._onChangeValueWrapper);
}}}}),
unregisterEvent:qx.core.Variant.select(h,
{"mshtml":function(r,
s){if(!r.__ki){var v=r.tagName.toLowerCase();
var s=r.type;
if(s===g||v===f||s===c||s===d){qx.bom.Event.removeNativeListener(r,
j,
this._onPropertyWrapper);
}
if(s!==c&&s!==d){qx.bom.Event.removeNativeListener(r,
a,
this._onChangeValueWrapper);
}delete r.__ki;
}},
"default":function(r,
s){if(s===b){qx.bom.Event.removeNativeListener(r,
b,
this._onInputWrapper);
}else if(s===a){if(r.type===d||r.type===c){qx.bom.Event.removeNativeListener(r,
a,
this._onChangeCheckedWrapper);
}else{qx.bom.Event.removeNativeListener(r,
a,
this._onChangeValueWrapper);
}}}}),
_onInput:function(w){var r=w.target;
qx.event.Registration.fireEvent(r,
b,
qx.event.type.Data,
[r.value]);
},
_onChangeValue:function(w){var r=w.target||w.srcElement;
var x=r.value;
if(r.type===k){var x=[];
for(var y=0,
z=r.options,
A=z.length;y<A;y++){if(z[y].selected){x.push(z[y].value);
}}}qx.event.Registration.fireEvent(r,
a,
qx.event.type.Data,
[x]);
},
_onChangeChecked:function(w){var r=w.target;
if(r.type===d){if(r.checked){qx.event.Registration.fireEvent(r,
a,
qx.event.type.Data,
[r.value]);
}}else{qx.event.Registration.fireEvent(r,
a,
qx.event.type.Data,
[r.checked]);
}},
_onProperty:qx.core.Variant.select(h,
{"mshtml":function(w){var r=w.target||w.srcElement;
var B=w.propertyName;
if(B===n&&(r.type===g||r.tagName.toLowerCase()===f)){if(!r.__inValueSet){qx.event.Registration.fireEvent(r,
b,
qx.event.type.Data,
[r.value]);
}}else if(B===m){if(r.type===c){qx.event.Registration.fireEvent(r,
a,
qx.event.type.Data,
[r.checked]);
}else if(r.checked){qx.event.Registration.fireEvent(r,
a,
qx.event.type.Data,
[r.value]);
}}},
"default":function(){}})},
defer:function(C){qx.event.Registration.addHandler(C);
}});
})();
(function(){var a="soft",
b="qx.client",
c="off",
d="",
e="input",
f="nowrap",
g="select",
h="qx.bom.Input",
i="normal",
j="textarea",
k="auto",
l='wrap';
qx.Class.define(h,
{statics:{__kj:{text:1,
textarea:1,
select:1,
checkbox:1,
radio:1,
password:1,
hidden:1,
submit:1,
image:1,
file:1,
search:1,
reset:1,
button:1},
create:function(m,
n,
o){{};
var n=n?qx.lang.Object.copy(n):{};
var p;
if(m===j||m===g){p=m;
}else{p=e;
n.type=m;
}return qx.bom.Element.create(p,
n,
o);
},
setValue:qx.core.Variant.select(b,
{"mshtml":function(q,
r){q.__kk=true;
q.value=r;
q.__kk=null;
},
"default":function(q,
r){q.value=r;
}}),
getValue:function(q){return q.value;
},
setWrap:qx.core.Variant.select(b,
{"mshtml":function(q,
s){q.wrap=s?a:c;
},
"gecko":function(q,
s){var t=s?a:c;
var u=s?d:k;
q.setAttribute(l,
t);
q.style.overflow=u;
},
"default":function(q,
s){q.style.whiteSpace=s?i:f;
}})}});
})();
(function(){var a="",
b="Number",
c='</div>',
d='" ',
e='<div>',
f="</div>",
g="div",
h="overflow: hidden;",
j='style="',
k="__kp",
l="_applyMaxCacheLines",
m="qx.ui.table.pane.Pane",
n="width: 100%;",
o="_applyVisibleRowCount",
p="__kl",
q='>',
r="line-height: ",
s="appear",
t='class="',
u="width:100%;",
v="px;",
w='<div ',
z="'>",
A="_applyFirstVisibleRow",
B="<div style='",
C=";position:relative;",
D="__ko";
qx.Class.define(m,
{extend:qx.ui.core.Widget,
construct:function(E){arguments.callee.base.call(this);
this.__kl=E;
this.__km=0;
this.__kn=0;
this.__ko=[];
},
properties:{firstVisibleRow:{check:b,
init:0,
apply:A},
visibleRowCount:{check:b,
init:0,
apply:o},
maxCacheLines:{check:b,
init:1000,
apply:l},
allowShrinkX:{refine:true,
init:false},
allowGrowY:{refine:true,
init:false},
allowShrinkY:{refine:true,
init:false}},
members:{__kn:null,
__km:null,
__kl:null,
__kp:null,
__kq:null,
__kr:null,
__ko:null,
__ks:0,
_applyFirstVisibleRow:function(F,
G){this.updateContent(false,
F-G);
},
_applyVisibleRowCount:function(F,
G){this.updateContent();
},
getPaneScroller:function(){return this.__kl;
},
getTable:function(){return this.__kl.getTable();
},
setFocusedCell:function(H,
I,
J){if(H!=this.__kr||I!=this.__kq){var K=this.__kq;
this.__kr=H;
this.__kq=I;
if(I!=K&&!J){if(I!==null&&K!==null){this.updateContent(false,
null,
K,
true);
this.updateContent(false,
null,
I,
true);
}else{this.updateContent();
}}}},
onSelectionChanged:function(){this.updateContent(false,
null,
null,
true);
},
onFocusChanged:function(){this.updateContent(false,
null,
null,
true);
},
setColumnWidth:function(H,
L){this.updateContent(true);
},
onColOrderChanged:function(){this.updateContent(true);
},
onPaneModelChanged:function(){this.updateContent(true);
},
onTableModelDataChanged:function(M,
N,
O,
P){this.__kt();
var Q=this.getFirstVisibleRow();
var R=this.getVisibleRowCount();
if(N==-1||N>=Q&&M<Q+R){this.updateContent();
}},
onTableModelMetaDataChanged:function(){this.updateContent(true);
},
_applyMaxCacheLines:function(F,
G){if(this.__ks>=F&&F!==-1){this.__kt();
}},
__kt:function(){this.__ko=[];
this.__ks=0;
},
__ku:function(I,
S,
T){if(!S&&!T&&this.__ko[I]){return this.__ko[I];
}else{return null;
}},
__kv:function(I,
U,
S,
T){if(!S&&!T&&!this.__ko[I]){this._applyMaxCacheLines(this.getMaxCacheLines());
this.__ko[I]=U;
this.__ks+=1;
}},
updateContent:function(V,
W,
X,
Y){if(V){this.__kt();
}if(W&&Math.abs(W)<=Math.min(10,
this.getVisibleRowCount())){this._scrollContent(W);
}else if(Y&&!this.getTable().getAlwaysUpdateCells()){this._updateRowStyles(X);
}else{this._updateAllRows();
}},
_updateRowStyles:function(X){var ba=this.getContentElement().getDomElement();
if(!ba||!ba.firstChild){this._updateAllRows();
return;
}var bb=this.getTable();
var bc=bb.getSelectionModel();
var bd=bb.getTableModel();
var be=bb.getDataRowRenderer();
var bf=ba.firstChild.childNodes;
var bg={table:bb};
var I=this.getFirstVisibleRow();
var bh=0;
var bi=bf.length;
if(X!=null){var bj=X-I;
if(bj>=0&&bj<bi){I=X;
bh=bj;
bi=bj+1;
}else{return;
}}
for(;bh<bi;bh++,
I++){bg.row=I;
bg.selected=bc.isSelectedIndex(I);
bg.focusedRow=(this.__kq==I);
bg.rowData=bd.getRowData(I);
be.updateDataRowElement(bg,
bf[bh]);
}},
_getRowsHtml:function(M,
R){var bb=this.getTable();
var bc=bb.getSelectionModel();
var bd=bb.getTableModel();
var bk=bb.getTableColumnModel();
var bl=this.getPaneScroller().getTablePaneModel();
var be=bb.getDataRowRenderer();
bd.prefetchRows(M,
M+R-1);
var bm=bb.getRowHeight();
var bn=bl.getColumnCount();
var bo=0;
var bp=[];
for(var bq=0;bq<bn;bq++){var H=bl.getColumnAtX(bq);
var br=bk.getColumnWidth(H);
bp.push({col:H,
xPos:bq,
editable:bd.isColumnEditable(H),
focusedCol:this.__kr==H,
styleLeft:bo,
styleWidth:br});
bo+=br;
}var bs=[];
for(var I=M;I<M+R;I++){var S=bc.isSelectedIndex(I);
var bt=(this.__kq==I);
var bu=this.__ku(I,
S,
bt);
if(bu){bs.push(bu);
continue;
}var bv=[];
var bg={table:bb};
bg.styleHeight=bm;
bg.row=I;
bg.selected=S;
bg.focusedRow=bt;
bg.rowData=bd.getRowData(I);
bv.push(w);
var bw=be.getRowClass(bg);
if(bw){bv.push(t,
bw,
d);
}var bx=be.createRowStyle(bg);
bx+=C+be.getRowHeightStyle(bm)+u;
if(bx){bv.push(j,
bx,
d);
}bv.push(q);
for(var bq=0;bq<bn;bq++){var by=bp[bq];
for(var bz in by){bg[bz]=by[bz];
}var H=bg.col;
bg.value=bd.getValue(H,
I);
var bA=bk.getDataCellRenderer(H);
bA.createDataCellHtml(bg,
bv);
}bv.push(c);
var U=bv.join(a);
this.__kv(I,
U,
S,
bt);
bs.push(U);
}return bs.join(a);
},
_scrollContent:function(bB){var bC=this.getContentElement().getDomElement();
if(!(bC&&bC.firstChild)){this._updateAllRows();
return;
}var bD=bC.firstChild;
var bE=bD.childNodes;
var R=this.getVisibleRowCount();
var M=this.getFirstVisibleRow();
var bF=this.getTable().getTableModel().getRowCount();
if(M+R>bF){this._updateAllRows();
return;
}var bG=bB<0?R+bB:0;
var bH=bB<0?0:R-bB;
for(bL=Math.abs(bB)-1;bL>=0;bL--){var bI=bE[bG];
try{bD.removeChild(bI);
}catch(exp){break;
}}if(!this.__kp){this.__kp=document.createElement(g);
}var bJ=e;
bJ+=this._getRowsHtml(M+bH,
Math.abs(bB));
bJ+=c;
this.__kp.innerHTML=bJ;
var bK=this.__kp.firstChild.childNodes;
if(bB>0){for(var bL=bK.length-1;bL>=0;bL--){var bI=bK[0];
bD.appendChild(bI);
}}else{for(var bL=bK.length-1;bL>=0;bL--){var bI=bK[bK.length-1];
bD.insertBefore(bI,
bD.firstChild);
}}if(this.__kq!==null){this._updateRowStyles(this.__kq-bB);
this._updateRowStyles(this.__kq);
}},
_updateAllRows:function(){var ba=this.getContentElement().getDomElement();
if(!ba){this.addListenerOnce(s,
arguments.callee,
this);
return;
}var bb=this.getTable();
var bd=bb.getTableModel();
var bl=this.getPaneScroller().getTablePaneModel();
var bn=bl.getColumnCount();
var bm=bb.getRowHeight();
var M=this.getFirstVisibleRow();
var R=this.getVisibleRowCount();
var bF=bd.getRowCount();
if(M+R>bF){R=Math.max(0,
bF-M);
}var bM=bl.getTotalWidth();
var bN;
if(R>0){bN=[B,
n,
(bb.getForceLineHeight()?r+bm+v:a),
h,
z,
this._getRowsHtml(M,
R),
f];
}else{bN=[];
}var bO=bN.join(a);
ba.innerHTML=bO;
this.setHeight(R*bm);
this.setWidth(bM);
this.__km=bn;
this.__kn=R;
}},
destruct:function(){this._disposeFields(k,
p,
D);
}});
})();
(function(){var a="hovered",
b="__kw",
c="qx.ui.table.pane.Header";
qx.Class.define(c,
{extend:qx.ui.core.Widget,
construct:function(d){arguments.callee.base.call(this);
this._setLayout(new qx.ui.layout.HBox());
this.__kw=d;
},
members:{__kw:null,
__kx:null,
__ky:null,
getPaneScroller:function(){return this.__kw;
},
getTable:function(){return this.__kw.getTable();
},
onColOrderChanged:function(){this._updateContent(true);
},
onPaneModelChanged:function(){this._updateContent(true);
},
onTableModelMetaDataChanged:function(){this._updateContent();
},
setColumnWidth:function(e,
f){var g=this.getHeaderWidgetAtColumn(e);
if(g!=null){g.setWidth(f);
}},
setMouseOverColumn:function(e){if(e!=this.__ky){if(this.__ky!=null){var h=this.getHeaderWidgetAtColumn(this.__ky);
if(h!=null){h.removeState(a);
}}
if(e!=null){this.getHeaderWidgetAtColumn(e).addState(a);
}this.__ky=e;
}},
getHeaderWidgetAtColumn:function(e){var i=this.getPaneScroller().getTablePaneModel().getX(e);
return this._getChildren()[i];
},
showColumnMoveFeedback:function(e,
j){var k=this.getContainerLocation();
if(this.__kx==null){var i=this.getPaneScroller().getTablePaneModel().getX(e);
var l=this._getChildren()[i];
var m=this.getTable().getTableModel();
var n=this.getTable().getTableColumnModel();
var o={xPos:i,
col:e,
name:m.getColumnName(e)};
var p=n.getHeaderCellRenderer(e);
var q=p.createHeaderCell(o);
var r=l.getBounds();
q.setWidth(r.width);
q.setHeight(r.height);
q.setZIndex(1000000);
q.setOpacity(0.8);
q.setLayoutProperties({top:k.top});
this.getApplicationRoot().add(q);
this.__kx=q;
}this.__kx.setLayoutProperties({left:k.left+j});
this.__kx.show();
},
hideColumnMoveFeedback:function(){if(this.__kx!=null){this.__kx.getLayoutParent().remove(this.__kx);
this.__kx.dispose();
this.__kx=null;
}},
isShowingColumnMoveFeedback:function(){return this.__kx!=null;
},
_updateContent:function(s){var m=this.getTable().getTableModel();
var n=this.getTable().getTableColumnModel();
var t=this.getPaneScroller().getTablePaneModel();
var u=this._getChildren();
var v=t.getColumnCount();
var w=m.getSortColumnIndex();
if(s){this._cleanUpCells();
}var o={};
o.sortedAscending=m.isSortAscending();
for(var j=0;j<v;j++){var e=t.getColumnAtX(j);
var y=n.getColumnWidth(e);
var p=n.getHeaderCellRenderer(e);
o.xPos=j;
o.col=e;
o.name=m.getColumnName(e);
o.editable=m.isColumnEditable(e);
o.sorted=(e==w);
var z=u[j];
if(z==null){z=p.createHeaderCell(o);
z.set({width:y});
this._add(z);
}else{p.updateHeaderCell(o,
z);
}}},
_cleanUpCells:function(){var u=this._getChildren();
for(var j=u.length-1;j>=0;j--){var l=u[j];
this._remove(l);
l.dispose();
}}},
destruct:function(){this._disposeObjects(b);
}});
})();
(function(){var a="Boolean",
b="resize-line",
c="mousedown",
d="mouseup",
g="qx.ui.table.pane.CellEvent",
h="scroll",
i="focus-indicator",
j="excluded",
k="scrollbar-y",
l="visible",
m="mousemove",
n="header",
o="editing",
p="click",
q="modelChanged",
r="scrollbar-x",
s="cellClick",
t="qx.event.type.ChangeEvent",
u="pane",
v="__kG",
w="__kz",
y="__kA",
z="mouseout",
A="changeHorizontalScrollBarVisible",
B="__kC",
C="bottom",
D="_applyScrollTimeout",
E="changeScrollX",
F="_applyTablePaneModel",
G="Integer",
H="dblclick",
I="mousewheel",
J="qx.ui.table.pane.Scroller",
K="__kT",
L="_applyShowCellFocusIndicator",
M="resize",
N="vertical",
O="__kD",
P="changeScrollY",
Q="__kF",
R="appear",
S="_top",
T="table-scroller",
U="cellDblclick",
V="__ld",
W="__kE",
X="horizontal",
Y="__kB",
ba="losecapture",
bb="contextmenu",
bc="disappear",
bd="_applyVerticalScrollBarVisible",
be="_applyHorizontalScrollBarVisible",
bf="cellContextmenu",
bg="close",
bh="ew-resize",
bi="changeTablePaneModel",
bj="qx.ui.table.pane.Model",
bk="changeVerticalScrollBarVisible";
qx.Class.define(J,
{extend:qx.ui.core.Widget,
construct:function(bl){arguments.callee.base.call(this);
this.__kz=bl;
var bm=new qx.ui.layout.Grid();
bm.setColumnFlex(0,
1);
bm.setRowFlex(1,
1);
this._setLayout(bm);
this.__kA=this._showChildControl(r);
this.__kB=this._showChildControl(k);
this.__kC=this._showChildControl(n);
this.__kD=this._showChildControl(u);
this._top=new qx.ui.container.Composite(new qx.ui.layout.HBox());
this._add(this._top,
{row:0,
column:0,
colSpan:2});
this.__kE=new qx.ui.table.pane.Clipper();
this.__kE.add(this.__kC);
this.__kE.addListener(ba,
this._onChangeCaptureHeader,
this);
this.__kE.addListener(m,
this._onMousemoveHeader,
this);
this.__kE.addListener(c,
this._onMousedownHeader,
this);
this.__kE.addListener(d,
this._onMouseupHeader,
this);
this.__kE.addListener(p,
this._onClickHeader,
this);
this._top.add(this.__kE,
{flex:1});
this.__kF=new qx.ui.table.pane.Clipper();
this.__kF.add(this.__kD);
this.__kF.addListener(I,
this._onMousewheel,
this);
this.__kF.addListener(m,
this._onMousemovePane,
this);
this.__kF.addListener(c,
this._onMousedownPane,
this);
this.__kF.addListener(d,
this._onMouseupPane,
this);
this.__kF.addListener(p,
this._onClickPane,
this);
this.__kF.addListener(bb,
this._onContextMenu,
this);
this.__kF.addListener(H,
this._onDblclickPane,
this);
this.__kF.addListener(M,
this._onResizePane,
this);
this._add(this.__kF,
{row:1,
column:0});
this.__kG=this._getChildControl(i);
this._getChildControl(b);
this._excludeChildControl(b);
this.addListener(z,
this._onMouseout,
this);
this.addListener(R,
this._onAppear,
this);
this.addListener(bc,
this._onDisappear,
this);
if(!this.__kH){this.__kH=qx.lang.Function.bind(this._oninterval,
this);
}this.initScrollTimeout();
},
statics:{MIN_COLUMN_WIDTH:10,
RESIZE_REGION_RADIUS:5,
CLICK_TOLERANCE:5,
HORIZONTAL_SCROLLBAR:1,
VERTICAL_SCROLLBAR:2},
events:{"changeScrollY":t,
"changeScrollX":t,
"cellClick":g,
"cellDblclick":g,
"cellContextmenu":g},
properties:{horizontalScrollBarVisible:{check:a,
init:true,
apply:be,
event:A},
verticalScrollBarVisible:{check:a,
init:true,
apply:bd,
event:bk},
tablePaneModel:{check:bj,
apply:F,
event:bi},
liveResize:{check:a,
init:false},
focusCellOnMouseMove:{check:a,
init:false},
selectBeforeFocus:{check:a,
init:false},
showCellFocusIndicator:{check:a,
init:true,
apply:L},
scrollTimeout:{check:G,
init:100,
apply:D},
appearance:{refine:true,
init:T}},
members:{__kI:null,
__kz:null,
__kJ:null,
__kK:null,
__kH:null,
__kL:null,
__kM:null,
__kN:null,
__kO:null,
__kP:null,
__kQ:null,
__kR:null,
__kS:null,
__kT:null,
__kU:null,
__kV:null,
__kW:null,
__kX:null,
__kY:null,
__la:null,
__lb:null,
__lc:null,
__ld:null,
__kA:null,
__kB:null,
__kC:null,
__kE:null,
__kD:null,
__kF:null,
__kG:null,
_createChildControlImpl:function(bn){var bo;
switch(bn){case n:bo=this.getTable().getNewTablePaneHeader()(this);
break;
case u:bo=this.getTable().getNewTablePane()(this);
break;
case i:bo=new qx.ui.table.pane.FocusIndicator(this);
bo.setUserBounds(0,
0,
0,
0);
bo.setZIndex(1000);
bo.addListener(d,
this._onMouseupFocusIndicator,
this);
this.__kF.add(bo);
bo.exclude();
break;
case b:bo=new qx.ui.core.Widget();
bo.setUserBounds(0,
0,
0,
0);
bo.setZIndex(1000);
this.__kF.add(bo);
break;
case r:bo=new qx.ui.core.ScrollBar(X).set({minWidth:0,
alignY:C});
bo.addListener(h,
this._onScrollX,
this);
this._add(bo,
{row:2,
column:0});
break;
case k:bo=new qx.ui.core.ScrollBar(N);
bo.addListener(h,
this._onScrollY,
this);
this._add(bo,
{row:1,
column:1});
break;
}return bo||arguments.callee.base.call(this,
bn);
},
_applyHorizontalScrollBarVisible:function(bp,
bq){this.__kA.setVisibility(bp?l:j);
if(!bp){this.setScrollY(0,
true);
}},
_applyVerticalScrollBarVisible:function(bp,
bq){this.__kB.setVisibility(bp?l:j);
if(!bp){this.setScrollX(0);
}},
_applyTablePaneModel:function(bp,
bq){if(bq!=null){bq.removeListener(q,
this._onPaneModelChanged,
this);
}bp.addListener(q,
this._onPaneModelChanged,
this);
},
_applyShowCellFocusIndicator:function(bp,
bq){if(bp){this._updateFocusIndicator();
}else{if(this.__kG){this.__kG.hide();
}}},
getScrollY:function(){return this.__kB.getPosition();
},
setScrollY:function(br,
bs){this.__kX=bs;
this.__kB.scrollTo(br);
if(bs){this._updateContent();
}this.__kX=false;
},
getScrollX:function(){return this.__kA.getPosition();
},
setScrollX:function(bt){this.__kA.scrollTo(bt);
},
getTable:function(){return this.__kz;
},
onColVisibilityChanged:function(){this.updateHorScrollBarMaximum();
this._updateFocusIndicator();
},
setColumnWidth:function(bu,
bv){this.__kC.setColumnWidth(bu,
bv);
this.__kD.setColumnWidth(bu,
bv);
var bw=this.getTablePaneModel();
var bx=bw.getX(bu);
if(bx!=-1){this.updateHorScrollBarMaximum();
this._updateFocusIndicator();
}},
onColOrderChanged:function(){this.__kC.onColOrderChanged();
this.__kD.onColOrderChanged();
this.updateHorScrollBarMaximum();
},
onTableModelDataChanged:function(by,
bz,
bA,
bB){this.__kD.onTableModelDataChanged(by,
bz,
bA,
bB);
var bC=this.getTable().getTableModel().getRowCount();
if(bC!=this.__kI){this.updateVerScrollBarMaximum();
if(this.getFocusedRow()>=bC){if(bC==0){this.setFocusedCell(null,
null);
}else{this.setFocusedCell(this.getFocusedColumn(),
bC-1);
}}this.__kI=bC;
}},
onSelectionChanged:function(){this.__kD.onSelectionChanged();
},
onFocusChanged:function(){this.__kD.onFocusChanged();
},
onTableModelMetaDataChanged:function(){this.__kC.onTableModelMetaDataChanged();
this.__kD.onTableModelMetaDataChanged();
},
_onPaneModelChanged:function(){this.__kC.onPaneModelChanged();
this.__kD.onPaneModelChanged();
},
_onResizePane:function(){this.updateHorScrollBarMaximum();
this.updateVerScrollBarMaximum();
this._updateContent();
this.__kC._updateContent();
},
updateHorScrollBarMaximum:function(){var bD=this.__kF.getInnerSize();
if(!bD){return ;
}var bE=this.getTablePaneModel().getTotalWidth();
var bF=this.__kA;
if(bD.width<bE){var bG=Math.max(0,
bE-bD.width);
bF.setMaximum(bG);
bF.setKnobFactor(bD.width/bE);
var bH=bF.getPosition();
bF.setPosition(Math.min(bH,
bG));
}else{bF.setMaximum(0);
bF.setKnobFactor(1);
bF.setPosition(0);
}},
updateVerScrollBarMaximum:function(){var bD=this.__kF.getInnerSize();
if(!bD){return ;
}var bC=this.getTable().getTableModel().getRowCount();
if(this.getTable().getKeepFirstVisibleRowComplete()){bC+=1;
}var bI=this.getTable().getRowHeight();
var bE=bC*bI;
var bF=this.__kB;
if(bD.height<bE){var bG=Math.max(0,
bE-bD.height);
bF.setMaximum(bG);
bF.setKnobFactor(bD.height/bE);
var bH=bF.getPosition();
bF.setPosition(Math.min(bH,
bG));
}else{bF.setMaximum(0);
bF.setKnobFactor(1);
bF.setPosition(0);
}},
onKeepFirstVisibleRowCompleteChanged:function(){this.updateVerScrollBarMaximum();
this._updateContent();
},
_onAppear:function(){this._startInterval(this.getScrollTimeout());
},
_onDisappear:function(){this._stopInterval();
},
_onScrollX:function(bJ){var bK=bJ.getData();
this.fireDataEvent(E,
bK,
bJ.getOldData());
this.__kE.scrollToX(bK);
this.__kF.scrollToX(bK);
},
_onScrollY:function(bJ){this.fireDataEvent(P,
bJ.getData(),
bJ.getOldData());
this._postponedUpdateContent();
},
_onMousewheel:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}this.__kB.scrollTo(this.__kB.getPosition()+((bJ.getWheelDelta()*3)*bl.getRowHeight()));
if(this.__kV&&this.getFocusCellOnMouseMove()){this._focusCellAtPagePos(this.__kV,
this.__kW);
}},
__le:function(bL){var bl=this.getTable();
var bM=this.__kC.getHeaderWidgetAtColumn(this.__kQ);
var bN=bM.getSizeHint().minWidth;
var bO=Math.max(bN,
this.__kS+bL-this.__kR);
if(this.getLiveResize()){var bP=bl.getTableColumnModel();
bP.setColumnWidth(this.__kQ,
bO);
}else{this.__kC.setColumnWidth(this.__kQ,
bO);
var bw=this.getTablePaneModel();
this._showResizeLine(bw.getColumnLeft(this.__kQ)+bO);
}this.__kR+=bO-this.__kS;
this.__kS=bO;
},
__lf:function(bL){var bQ=qx.ui.table.pane.Scroller.CLICK_TOLERANCE;
if(this.__kC.isShowingColumnMoveFeedback()||bL>this.__kP+bQ||bL<this.__kP-bQ){this.__kM+=bL-this.__kP;
this.__kC.showColumnMoveFeedback(this.__kL,
this.__kM);
var bR=this.__kz.getTablePaneScrollerAtPageX(bL);
if(this.__kO&&this.__kO!=bR){this.__kO.hideColumnMoveFeedback();
}
if(bR!=null){this.__kN=bR.showColumnMoveFeedback(bL);
}else{this.__kN=null;
}this.__kO=bR;
this.__kP=bL;
}},
_onMousemoveHeader:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}var bS=false;
var bT=null;
var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
this.__kV=bL;
this.__kW=bU;
if(this.__kQ!=null){this.__le(bL);
bS=true;
}else if(this.__kL!=null){this.__lf(bL);
}else{var bV=this._getResizeColumnForPageX(bL);
if(bV!=-1){bS=true;
}else{var bW=bl.getTableModel();
var bu=this._getColumnForPageX(bL);
if(bu!=null&&bW.isColumnSortable(bu)){bT=bu;
}}}var bX=bS?bh:null;
this.getApplicationRoot().setGlobalCursor(bX);
this.setCursor(bX);
this.__kC.setMouseOverColumn(bT);
},
_onMousemovePane:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
this.__kV=bL;
this.__kW=bU;
var bY=this._getRowForPagePos(bL,
bU);
if(bY!=null&&this._getColumnForPageX(bL)!=null){if(this.getFocusCellOnMouseMove()){this._focusCellAtPagePos(bL,
bU);
}}this.__kC.setMouseOverColumn(null);
},
_onMousedownHeader:function(bJ){if(!this.getTable().getEnabled()){return;
}var bL=bJ.getDocumentLeft();
var bV=this._getResizeColumnForPageX(bL);
if(bV!=-1){this._startResizeHeader(bV,
bL);
}else{var ca=this._getColumnForPageX(bL);
if(ca!=null){this._startMoveHeader(ca,
bL);
}}},
_startResizeHeader:function(bV,
bL){var bP=this.getTable().getTableColumnModel();
this.__kQ=bV;
this.__kR=bL;
this.__kS=bP.getColumnWidth(this.__kQ);
this.__kE.capture();
},
_startMoveHeader:function(ca,
bL){this.__kL=ca;
this.__kP=bL;
this.__kM=this.getTablePaneModel().getColumnLeft(ca);
this.__kE.capture();
},
_onMousedownPane:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}
if(this.isEditing()){this.stopEditing();
}var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
var bY=this._getRowForPagePos(bL,
bU);
var bu=this._getColumnForPageX(bL);
if(bY!==null){this.__kT={row:bY,
col:bu};
var cb=this.getSelectBeforeFocus();
if(cb){bl.getSelectionManager().handleMouseDown(bY,
bJ);
}if(!this.getFocusCellOnMouseMove()){this._focusCellAtPagePos(bL,
bU);
}
if(!cb){bl.getSelectionManager().handleMouseDown(bY,
bJ);
}}},
_onMouseupFocusIndicator:function(bJ){if(this.__kT&&this.__kG.getRow()==this.__kT.row&&this.__kG.getColumn()==this.__kT.col){this.__kT={};
this.fireEvent(s,
qx.ui.table.pane.CellEvent,
[this,
bJ,
this.__kT.row,
this.__kT.col],
true);
}},
_onChangeCaptureHeader:function(bJ){if(this.__kQ!=null&&bJ.getData()==false){this._stopResizeHeader();
}
if(this.__kL!=null&&bJ.getData()==false){this._stopMoveHeader();
}},
_stopResizeHeader:function(){var bP=this.getTable().getTableColumnModel();
if(!this.getLiveResize()){this._hideResizeLine();
bP.setColumnWidth(this.__kQ,
this.__kS);
}this.__kQ=null;
this.__kE.releaseCapture();
this.getApplicationRoot().setGlobalCursor(null);
this.setCursor(null);
},
_stopMoveHeader:function(){var bP=this.getTable().getTableColumnModel();
var bw=this.getTablePaneModel();
this.__kC.hideColumnMoveFeedback();
if(this.__kO){this.__kO.hideColumnMoveFeedback();
}
if(this.__kN!=null){var cc=bw.getFirstColumnX()+bw.getX(this.__kL);
var cd=this.__kN;
if(cd!=cc&&cd!=cc+1){var ce=bP.getVisibleColumnAtX(cc);
var cf=bP.getVisibleColumnAtX(cd);
var cg=bP.getOverallX(ce);
var ch=(cf!=null)?bP.getOverallX(cf):bP.getOverallColumnCount();
if(ch>cg){ch--;
}bP.moveColumn(cg,
ch);
}}this.__kL=null;
this.__kN=null;
this.__kE.releaseCapture();
},
_onMouseupPane:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}var bY=this._getRowForPagePos(bJ.getDocumentLeft(),
bJ.getDocumentTop());
if(bY!=-1&&bY!=null&&this._getColumnForPageX(bJ.getDocumentLeft())!=null){bl.getSelectionManager().handleMouseUp(bY,
bJ);
}},
_onMouseupHeader:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}
if(this.__kQ!=null){this._stopResizeHeader();
this.__kU=true;
}else if(this.__kL!=null){this._stopMoveHeader();
}},
_onClickHeader:function(bJ){if(this.__kU){this.__kU=false;
return;
}var bl=this.getTable();
if(!bl.getEnabled()){return;
}var bW=bl.getTableModel();
var bL=bJ.getDocumentLeft();
var bV=this._getResizeColumnForPageX(bL);
if(bV==-1){var bu=this._getColumnForPageX(bL);
if(bu!=null&&bW.isColumnSortable(bu)){var ci=bW.getSortColumnIndex();
var cj=(bu!=ci)?true:!bW.isSortAscending();
bW.sortByColumn(bu,
cj);
bl.getSelectionModel().clearSelection();
}}},
_onClickPane:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
var bY=this._getRowForPagePos(bL,
bU);
var bu=this._getColumnForPageX(bL);
if(bY!=null&&bu!=null){bl.getSelectionManager().handleClick(bY,
bJ);
if(this.__kT&&bY==this.__kT.row&&bu==this.__kT.col){this.__kT={};
this.fireEvent(s,
qx.ui.table.pane.CellEvent,
[this,
bJ,
bY,
bu],
true);
}}},
_onContextMenu:function(bJ){var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
var bY=this._getRowForPagePos(bL,
bU);
var bu=this._getColumnForPageX(bL);
if(this.__kT&&bY==this.__kT.row&&bu==this.__kT.col){this.__kT={};
this.fireEvent(bf,
qx.ui.table.pane.CellEvent,
[this,
bJ,
bY,
bu],
true);
}},
_onDblclickPane:function(bJ){var bL=bJ.getDocumentLeft();
var bU=bJ.getDocumentTop();
this._focusCellAtPagePos(bL,
bU);
this.startEditing();
var bY=this._getRowForPagePos(bL,
bU);
if(bY!=-1&&bY!=null){this.fireEvent(U,
qx.ui.table.pane.CellEvent,
[this,
bJ,
bY],
true);
}},
_onMouseout:function(bJ){var bl=this.getTable();
if(!bl.getEnabled()){return;
}if(this.__kQ==null){this.setCursor(null);
this.getApplicationRoot().setGlobalCursor(null);
}this.__kC.setMouseOverColumn(null);
},
_showResizeLine:function(bx){var ck=this._showChildControl(b);
var bv=ck.getWidth();
var cl=this.__kF.getBounds();
ck.setUserBounds(bx-Math.round(bv/2),
0,
bv,
cl.height);
},
_hideResizeLine:function(){this._excludeChildControl(b);
},
showColumnMoveFeedback:function(bL){var bw=this.getTablePaneModel();
var bP=this.getTable().getTableColumnModel();
var cm=this.__kD.getContainerLocation().left;
var cn=bw.getColumnCount();
var co=0;
var cp=0;
var cq=cm;
for(var cr=0;cr<cn;cr++){var bu=bw.getColumnAtX(cr);
var cs=bP.getColumnWidth(bu);
if(bL<cq+cs/2){break;
}cq+=cs;
co=cr+1;
cp=cq-cm;
}var ct=this.__kF.getContainerLocation().left;
var cu=this.__kF.getBounds().width;
var bt=ct-cm;
cp=qx.lang.Number.limit(cp,
bt+2,
bt+cu-1);
this._showResizeLine(cp);
return bw.getFirstColumnX()+co;
},
hideColumnMoveFeedback:function(){this._hideResizeLine();
},
_focusCellAtPagePos:function(bL,
bU){var bY=this._getRowForPagePos(bL,
bU);
if(bY!=-1&&bY!=null){var bu=this._getColumnForPageX(bL);
this.__kz.setFocusedCell(bu,
bY);
}},
setFocusedCell:function(bu,
bY){if(!this.isEditing()){this.__kD.setFocusedCell(bu,
bY,
this.__kK);
this.__kY=bu;
this.__la=bY;
this._updateFocusIndicator();
}},
getFocusedColumn:function(){return this.__kY;
},
getFocusedRow:function(){return this.__la;
},
scrollCellVisible:function(bu,
bY){var bw=this.getTablePaneModel();
var cr=bw.getX(bu);
if(cr!=-1){var cv=this.__kF.getInnerSize();
if(!cv){return;
}var bP=this.getTable().getTableColumnModel();
var cw=bw.getColumnLeft(bu);
var cs=bP.getColumnWidth(bu);
var bI=this.getTable().getRowHeight();
var cx=bY*bI;
var bt=this.getScrollX();
var br=this.getScrollY();
var cy=Math.min(cw,
cw+cs-cv.width);
var cz=cw;
this.setScrollX(Math.max(cy,
Math.min(cz,
bt)));
var cA=cx+bI-cv.height;
if(this.getTable().getKeepFirstVisibleRowComplete()){cA+=bI;
}var cB=cx;
this.setScrollY(Math.max(cA,
Math.min(cB,
br)),
true);
}},
isEditing:function(){return this.__lb!=null;
},
startEditing:function(){var bl=this.getTable();
var bW=bl.getTableModel();
var bu=this.__kY;
if(!this.isEditing()&&(bu!=null)&&bW.isColumnEditable(bu)){var bY=this.__la;
var cr=this.getTablePaneModel().getX(bu);
var bp=bW.getValue(bu,
bY);
this.__lc=bl.getTableColumnModel().getCellEditorFactory(bu);
var cC={col:bu,
row:bY,
xPos:cr,
value:bp,
table:bl};
this.__lb=this.__lc.createCellEditor(cC);
if(this.__lb===null){return false;
}else if(this.__lb instanceof qx.ui.window.Window){this.__lb.setModal(true);
this.__lb.setShowClose(false);
this.__lb.addListener(bg,
this._onCellEditorModalWindowClose,
this);
var cD=bl.getModalCellEditorPreOpenFunction();
if(cD!=null){cD(this.__lb,
cC);
}this.__lb.open();
}else{var cE=this.__kG.getInnerSize();
this.__lb.setUserBounds(0,
0,
cE.width,
cE.height);
this.__kG.addListener(c,
function(bJ){bJ.stopPropagation();
});
this.__kG.add(this.__lb);
this.__kG.addState(o);
this.__kG.setKeepActive(false);
this.__lb.focus();
this.__lb.activate();
}return true;
}return false;
},
stopEditing:function(){this.flushEditor();
this.cancelEditing();
},
flushEditor:function(){if(this.isEditing()){var bp=this.__lc.getCellEditorValue(this.__lb);
this.getTable().getTableModel().setValue(this.__kY,
this.__la,
bp);
this.__kz.focus();
}},
cancelEditing:function(){if(this.isEditing()&&!this.__lb.pendingDispose){if(this._cellEditorIsModalWindow){this.__lb.destroy();
this.__lb=null;
this.__lc=null;
this.__lb.pendingDispose=true;
}else{this.__kG.removeState(o);
this.__kG.setKeepActive(true);
this.__lb.destroy();
this.__lb=null;
this.__lc=null;
}}},
_onCellEditorModalWindowClose:function(bJ){this.stopEditing();
},
_getColumnForPageX:function(bL){var bP=this.getTable().getTableColumnModel();
var bw=this.getTablePaneModel();
var cn=bw.getColumnCount();
var cq=this.__kC.getContainerLocation().left;
for(var bx=0;bx<cn;bx++){var bu=bw.getColumnAtX(bx);
var cs=bP.getColumnWidth(bu);
cq+=cs;
if(bL<cq){return bu;
}}return null;
},
_getResizeColumnForPageX:function(bL){var bP=this.getTable().getTableColumnModel();
var bw=this.getTablePaneModel();
var cn=bw.getColumnCount();
var cq=this.__kC.getContainerLocation().left;
var cF=qx.ui.table.pane.Scroller.RESIZE_REGION_RADIUS;
for(var bx=0;bx<cn;bx++){var bu=bw.getColumnAtX(bx);
var cs=bP.getColumnWidth(bu);
cq+=cs;
if(bL>=(cq-cF)&&bL<=(cq+cF)){return bu;
}}return -1;
},
_getRowForPagePos:function(bL,
bU){var cG=this.__kD.getContentLocation();
if(bL<cG.left||bL>cG.right){return null;
}
if(bU>=cG.top&&bU<=cG.bottom){var bI=this.getTable().getRowHeight();
var br=this.__kB.getPosition();
if(this.getTable().getKeepFirstVisibleRowComplete()){br=Math.floor(br/bI)*bI;
}var cH=br+bU-cG.top;
var bY=Math.floor(cH/bI);
var bC=this.getTable().getTableModel().getRowCount();
return (bY<bC)?bY:null;
}var cI=this.__kC.getContainerLocation();
if(bU>=cI.top&&bU<=cI.bottom&&bL<=cI.right){return -1;
}return null;
},
setTopRightWidget:function(cJ){var cK=this.__ld;
if(cK!=null){this._top.remove(cK);
}
if(cJ!=null){this._top.add(cJ);
}this.__ld=cJ;
},
getTopRightWidget:function(){return this.__ld;
},
getHeader:function(){return this.__kC;
},
getTablePane:function(){return this.__kD;
},
getVerticalScrollBarWidth:function(){var bF=this.__kB;
return bF.isVisible()?(bF.getSizeHint().width||0):0;
},
getNeededScrollBars:function(cL,
cM){var cN=this.__kB.getSizeHint().width;
var cv=this.__kF.getInnerSize();
var cO=cv.width;
if(this.getVerticalScrollBarVisible()){cO+=cN;
}var cP=cv.height;
if(this.getHorizontalScrollBarVisible()){cP+=cN;
}var cQ=this.getTablePaneModel().getTotalWidth();
var cR=this.getTable().getRowHeight()*this.getTable().getTableModel().getRowCount();
var cS=false;
var cT=false;
if(cQ>cO){cS=true;
if(cR>cP-cN){cT=true;
}}else if(cR>cP){cT=true;
if(!cM&&(cQ>cO-cN)){cS=true;
}}var cU=qx.ui.table.pane.Scroller.HORIZONTAL_SCROLLBAR;
var cV=qx.ui.table.pane.Scroller.VERTICAL_SCROLLBAR;
return ((cL||cS)?cU:0)|((cM||!cT)?0:cV);
},
_applyScrollTimeout:function(bp,
bq){this._startInterval(bp);
},
_startInterval:function(cW){this._stopInterval();
if(cW){this.__kJ=window.setInterval(this.__kH,
cW);
}},
_stopInterval:function(){if(this.__kJ){window.clearInterval(this.__kJ);
this.__kJ=null;
}},
_postponedUpdateContent:function(){this._updateContent();
},
_oninterval:function(){if(this.__kK&&!this.__kD._layoutPending){this.__kK=false;
this._updateContent();
}},
_updateContent:function(){var bD=this.__kF.getInnerSize();
if(!bD){return;
}var cR=bD.height;
var bt=this.__kA.getPosition();
var br=this.__kB.getPosition();
var bI=this.getTable().getRowHeight();
var by=Math.floor(br/bI);
var cX=this.__kD.getFirstVisibleRow();
this.__kD.setFirstVisibleRow(by);
var cY=Math.ceil(cR/bI);
var da=0;
var db=this.getTable().getKeepFirstVisibleRowComplete();
if(!db){cY++;
da=br%bI;
}this.__kD.setVisibleRowCount(cY);
if(by!=cX){this._updateFocusIndicator();
}this.__kF.scrollToX(bt);
if(!db){this.__kF.scrollToY(da);
}},
_updateFocusIndicator:function(){if(!this.getShowCellFocusIndicator()){return;
}var bl=this.getTable();
if(!bl.getEnabled()){return;
}this.__kG.moveToCell(this.__kY,
this.__la);
}},
destruct:function(){this._stopInterval();
var dc=this.getTablePaneModel();
if(dc){dc.dispose();
}this._disposeFields(K);
this._disposeObjects(w,
y,
Y,
W,
Q,
v,
B,
O,
S,
V);
}});
})();
(function(){var a="qx.ui.table.pane.Clipper";
qx.Class.define(a,
{extend:qx.ui.container.Composite,
construct:function(){arguments.callee.base.call(this,
new qx.ui.layout.Grow());
},
members:{scrollToX:function(b){this.getContentElement().scrollToX(b,
false);
},
scrollToY:function(b){this.getContentElement().scrollToY(b,
true);
}}});
})();
(function(){var a="Integer",
b="__lg",
c="Escape",
d="keypress",
f="Enter",
g="excluded",
h="qx.ui.table.pane.FocusIndicator";
qx.Class.define(h,
{extend:qx.ui.container.Composite,
construct:function(i){arguments.callee.base.call(this);
this.__lg=i;
this.setKeepActive(true);
this.addListener(d,
this._onKeyPress,
this);
},
properties:{visibility:{refine:true,
init:g},
row:{check:a,
nullable:true},
column:{check:a,
nullable:true}},
members:{__lg:null,
_onKeyPress:function(j){var k=j.getKeyIdentifier();
if(k!==c&&k!==f){j.stopPropagation();
}},
moveToCell:function(l,
m){if(l==null){this.hide();
this.setRow(null);
this.setColumn(null);
}else{var n=this.__lg.getTablePaneModel().getX(l);
if(n==-1){this.hide();
this.setRow(null);
this.setColumn(null);
}else{var o=this.__lg.getTable();
var p=o.getTableColumnModel();
var q=this.__lg.getTablePaneModel();
var r=this.__lg.getTablePane().getFirstVisibleRow();
var s=o.getRowHeight();
this.setUserBounds(q.getColumnLeft(l)-2,
(m-r)*s-2,
p.getColumnWidth(l)+3,
s+3);
this.show();
this.setRow(m);
this.setColumn(l);
}}}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="Integer",
b="qx.ui.table.pane.CellEvent";
qx.Class.define(b,
{extend:qx.event.type.Mouse,
properties:{row:{check:a,
nullable:true},
column:{check:a,
nullable:true}},
members:{init:function(c,
d,
e,
f){d.clone(this);
this.setBubbles(false);
if(e!=null){this.setRow(e);
}else{this.setRow(c._getRowForPagePos(this.getDocumentLeft(),
this.getDocumentTop()));
}
if(f!=null){this.setColumn(f);
}else{this.setColumn(c._getColumnForPageX(this.getDocumentLeft()));
}}}});
})();
(function(){var a="qx.lang.Number";
qx.Bootstrap.define(a,
{statics:{isInRange:function(b,
c,
d){return b>=c&&b<=d;
},
isBetweenRange:function(b,
c,
d){return b>c&&b<d;
},
limit:function(b,
c,
d){if(d!=null&&b>d){return d;
}else if(c!=null&&b<c){return c;
}else{return b;
}}}});
})();
(function(){var a="Number",
b="__lh",
c="qx.event.type.Event",
d="_applyFirstColumnX",
e="Integer",
f="qx.ui.table.pane.Model",
g="_applyMaxColumnCount",
h="visibilityChangedPre";
qx.Class.define(f,
{extend:qx.core.Object,
construct:function(i){arguments.callee.base.call(this);
i.addListener(h,
this._onColVisibilityChanged,
this);
this.__lh=i;
},
events:{"modelChanged":c},
statics:{EVENT_TYPE_MODEL_CHANGED:"modelChanged"},
properties:{firstColumnX:{check:e,
init:0,
apply:d},
maxColumnCount:{check:a,
init:-1,
apply:g}},
members:{__li:null,
__lh:null,
_applyFirstColumnX:function(j,
k){this.__li=null;
this.fireEvent(qx.ui.table.pane.Model.EVENT_TYPE_MODEL_CHANGED);
},
_applyMaxColumnCount:function(j,
k){this.__li=null;
this.fireEvent(qx.ui.table.pane.Model.EVENT_TYPE_MODEL_CHANGED);
},
setTableColumnModel:function(i){this.__lh=i;
},
_onColVisibilityChanged:function(l){this.__li=null;
this.fireEvent(qx.ui.table.pane.Model.EVENT_TYPE_MODEL_CHANGED);
},
getColumnCount:function(){if(this.__li==null){var m=this.getFirstColumnX();
var n=this.getMaxColumnCount();
var o=this.__lh.getVisibleColumnCount();
if(n==-1||(m+n)>o){this.__li=o-m;
}else{this.__li=n;
}}return this.__li;
},
getColumnAtX:function(p){var m=this.getFirstColumnX();
return this.__lh.getVisibleColumnAtX(m+p);
},
getX:function(q){var m=this.getFirstColumnX();
var n=this.getMaxColumnCount();
var r=this.__lh.getVisibleX(q)-m;
if(r>=0&&(n==-1||r<n)){return r;
}else{return -1;
}},
getColumnLeft:function(q){var s=0;
var t=this.getColumnCount();
for(var r=0;r<t;r++){var u=this.getColumnAtX(r);
if(u==q){return s;
}s+=this.__lh.getColumnWidth(u);
}return -1;
},
getTotalWidth:function(){var v=0;
var t=this.getColumnCount();
for(var r=0;r<t;r++){var q=this.getColumnAtX(r);
v+=this.__lh.getColumnWidth(q);
}return v;
}},
destruct:function(){this._disposeObjects(b);
}});
})();
(function(){var a="checked",
b="String",
c="menu-checkbox",
d="_applyChecked",
f="Boolean",
g="changeName",
h="changeChecked",
i="changeValue",
j="qx.ui.menu.CheckBox";
qx.Class.define(j,
{extend:qx.ui.menu.AbstractButton,
implement:qx.ui.form.IFormElement,
construct:function(k,
l){arguments.callee.base.call(this);
if(k!=null){this.setLabel(k);
}
if(l!=null){this.setMenu(l);
}},
properties:{appearance:{refine:true,
init:c},
value:{check:b,
nullable:true,
event:i},
name:{check:b,
nullable:true,
event:g},
checked:{check:f,
init:false,
apply:d,
event:h}},
members:{_applyChecked:function(m,
n){m?this.addState(a):this.removeState(a);
},
_onMouseUp:function(o){if(o.isLeftPressed()){this.toggleChecked();
}},
_onKeyPress:function(o){this.toggleChecked();
}}});
})();
(function(){var a="appear",
b="columnVisibilityMenuCreateEnd",
c="tableWidthChanged",
d="verticalScrollBarChanged",
e="qx.ui.table.columnmodel.resizebehavior.Abstract",
f="qx.ui.table.columnmodel.Resize",
g="_applyBehavior",
h="__ll",
i="visibilityChanged",
j="Reset column widths",
k="changeBehavior",
l="table-column-reset-button",
m="widthChanged",
n="execute";
qx.Class.define(f,
{extend:qx.ui.table.columnmodel.Basic,
include:qx.locale.MTranslation,
construct:function(){arguments.callee.base.call(this);
this.__lj=false;
this.__lk=false;
},
properties:{behavior:{check:e,
init:null,
nullable:true,
apply:g,
event:k}},
members:{__lk:null,
__lj:null,
__ll:null,
_applyBehavior:function(p,
q){if(q!=null){q.dispose();
q=null;
}p._setNumColumns(this.getOverallColumnCount());
},
init:function(r,
s){arguments.callee.base.call(this,
r);
if(this.getBehavior()==null){this.setBehavior(new qx.ui.table.columnmodel.resizebehavior.Default());
}this.__ll=s;
s.addListener(a,
this._onappear,
this);
s.addListener(c,
this._onTableWidthChanged,
this);
s.addListener(d,
this._onverticalscrollbarchanged,
this);
this.addListener(m,
this._oncolumnwidthchanged,
this);
this.addListener(i,
this._onvisibilitychanged,
this);
this.__ll.addListener(b,
this._addResetColumnWidthButton,
this);
this.getBehavior()._setNumColumns(r);
},
getTable:function(){return this.__ll;
},
_addResetColumnWidthButton:function(t){var u=t.getData();
var v=u.menu;
var w;
w=new qx.ui.menu.Separator();
v.add(w);
w=new qx.ui.menu.Button(this.tr(j)).set({appearance:l});
v.add(w);
w.addListener(n,
this._onappear,
this);
},
_onappear:function(t){if(this.__lj){return ;
}this.__lj=true;
{};
this.getBehavior().onAppear(this,
t,
t.getType()!==a);
this.__ll._updateScrollerWidths();
this.__ll._updateScrollBarVisibility();
this.__lj=false;
this.__lk=true;
},
_onTableWidthChanged:function(t){if(this.__lj||!this.__lk){return ;
}this.__lj=true;
{};
this.getBehavior().onTableWidthChanged(this,
t);
this.__lj=false;
},
_onverticalscrollbarchanged:function(t){if(this.__lj||!this.__lk){return ;
}this.__lj=true;
{};
this.getBehavior().onVerticalScrollBarChanged(this,
t);
qx.event.Timer.once(function(){if(this.__ll&&!this.__ll.isDisposed()){this.__ll._updateScrollerWidths();
this.__ll._updateScrollBarVisibility();
}},
this,
0);
this.__lj=false;
},
_oncolumnwidthchanged:function(t){if(this.__lj||!this.__lk){return ;
}this.__lj=true;
{};
this.getBehavior().onColumnWidthChanged(this,
t);
this.__lj=false;
},
_onvisibilitychanged:function(t){if(this.__lj||!this.__lk){return ;
}this.__lj=true;
{};
this.getBehavior().onVisibilityChanged(this,
t);
this.__lj=false;
}},
settings:{"qx.tableResizeDebug":false},
destruct:function(){this._disposeFields(h);
}});
})();
(function(){var a="auto",
b="string",
c="number",
d="*",
e="qx.ui.core.ColumnData";
qx.Class.define(e,
{extend:qx.ui.core.LayoutItem,
construct:function(){arguments.callee.base.call(this);
this.setColumnWidth(a);
},
members:{__lm:null,
renderLayout:function(f,
g,
h,
i){this.__lm=h;
},
getComputedWidth:function(){return this.__lm;
},
setColumnWidth:function(h){var j=null;
var k=null;
if(typeof h==c){this.setWidth(h);
}else if(typeof h==b){if(h==a){j=1;
}else{var l=h.match(/^[0-9]+(?:\.[0-9]+)?([%\*])$/);
if(l){if(l[1]==d){j=parseFloat(h);
}else{k=h;
}}}}this.setLayoutProperties({flex:j,
width:k});
}}});
})();
(function(){var a="qx.ui.table.columnmodel.resizebehavior.Abstract",
b="abstract";
qx.Class.define(a,
{type:b,
extend:qx.core.Object,
members:{_setNumColumns:function(c){throw new Error("_setNumColumns is abstract");
},
onAppear:function(d,
e,
f){throw new Error("onAppear is abstract");
},
onTableWidthChanged:function(d,
e){throw new Error("onTableWidthChanged is abstract");
},
onVerticalScrollBarChanged:function(d,
e){throw new Error("onVerticalScrollBarChanged is abstract");
},
onColumnWidthChanged:function(d,
e){throw new Error("onColumnWidthChanged is abstract");
},
onVisibilityChanged:function(d,
e){throw new Error("onVisibilityChanged is abstract");
},
_getAvailableWidth:function(d){var g=d.getTable();
var h=g._getPaneScrollerArr();
var i=h[0].getLayoutParent().getBounds().width;
var j=h[h.length-1];
var k=j.getTopRightWidget();
var l=k&&k.getBounds()?k.getBounds().width:0;
var m=j.getVerticalScrollBarWidth();
return i-Math.max(l,
m);
}}});
})();
(function(){var a="Function",
b="Boolean",
c="minWidth",
d="width",
e="qx.ui.table.columnmodel.resizebehavior.Default",
f="__lo",
g="maxWidth",
h="__ln";
qx.Class.define(e,
{extend:qx.ui.table.columnmodel.resizebehavior.Abstract,
construct:function(){arguments.callee.base.call(this);
this.__ln=[];
this.__lo=new qx.ui.layout.HBox();
this.__lo.connectToWidget(this);
},
statics:{MIN_WIDTH:10},
properties:{newResizeBehaviorColumnData:{check:a,
init:function(j){return new qx.ui.core.ColumnData();
}},
initializeWidthsOnEveryAppear:{check:b,
init:false}},
members:{__lo:null,
__lp:null,
__ln:null,
__lq:false,
setWidth:function(k,
m){if(k>=this.__ln.length){throw new Error("Column number out of range");
}this.__ln[k].setColumnWidth(m);
},
setMinWidth:function(k,
m){if(k>=this.__ln.length){throw new Error("Column number out of range");
}this.__ln[k].setMinWidth(m);
},
setMaxWidth:function(k,
m){if(k>=this.__ln.length){throw new Error("Column number out of range");
}this.__ln[k].setMaxWidth(m);
},
set:function(k,
n){for(var o in n){switch(o){case d:this.setWidth(k,
n[o]);
break;
case c:this.setMinWidth(k,
n[o]);
break;
case g:this.setMaxWidth(k,
n[o]);
break;
default:throw new Error("Unknown property: "+o);
}}},
onAppear:function(p,
q,
r){if(r===true||!this.__lq||this.getInitializeWidthsOnEveryAppear()){this._computeColumnsFlexWidth(p,
q);
this.__lq=true;
}},
onTableWidthChanged:function(p,
q){this._computeColumnsFlexWidth(p,
q);
},
onVerticalScrollBarChanged:function(p,
q){this._computeColumnsFlexWidth(p,
q);
},
onColumnWidthChanged:function(p,
q){this._extendNextColumn(p,
q);
},
onVisibilityChanged:function(p,
q){var s=q.getData();
if(s.visible){this._computeColumnsFlexWidth(p,
q);
return;
}this._extendLastColumn(p,
q);
},
_setNumColumns:function(t){if(t<=this.__ln.length){this.__ln.splice(t,
this.__ln.length);
return;
}for(var u=this.__ln.length;u<t;u++){this.__ln[u]=this.getNewResizeBehaviorColumnData()();
this.__ln[u].columnNumber=u;
}},
getLayoutChildren:function(){return this.__lp;
},
_computeColumnsFlexWidth:function(p,
q){{};
var v=p.getVisibleColumns();
var w=v.length;
var u,
x;
var y=[];
for(u=0;u<w;u++){y.push(this.__ln[v[u]]);
}this.__lp=y;
var m=this._getAvailableWidth(p);
this.__lo.renderLayout(m,
100);
for(u=0,
x=y.length;u<x;u++){var z=y[u].getComputedWidth();
p.setColumnWidth(v[u],
z);
{};
}},
_extendNextColumn:function(p,
q){var s=q.getData();
var v=p.getVisibleColumns();
var m=this._getAvailableWidth(p);
var t=v.length;
if(s.newWidth>s.oldWidth){return ;
}var u;
var A;
var B=0;
for(u=0;u<t;u++){B+=p.getColumnWidth(v[u]);
}if(B<m){for(u=0;u<v.length;u++){if(v[u]==s.col){A=v[u+1];
break;
}}
if(A){var C=(m-(B-p.getColumnWidth(A)));
p.setColumnWidth(A,
C);
}}},
_extendLastColumn:function(p,
q){var s=q.getData();
if(s.visible){return;
}var v=p.getVisibleColumns();
var m=this._getAvailableWidth(p);
var t=v.length;
var u;
var D;
var B=0;
for(u=0;u<t;u++){B+=p.getColumnWidth(v[u]);
}if(B<m){D=v[v.length-1];
var C=(m-(B-p.getColumnWidth(D)));
p.setColumnWidth(D,
C);
}}},
destruct:function(){this._disposeFields(h);
this._disposeObjects(f);
}});
})();
(function(){var a="_applyStyle",
b="repeat",
c="px",
d="qx.ui.decoration.Background",
e="",
f="scale",
g="no-repeat",
h="position:absolute;top:0;left:0;",
i="repeat-x",
j="repeat-y",
k="Color",
l="String";
qx.Class.define(d,
{extend:qx.core.Object,
implement:[qx.ui.decoration.IDecorator],
construct:function(m){arguments.callee.base.call(this);
if(m!=null){this.setBackgroundColor(m);
}},
properties:{backgroundImage:{check:l,
nullable:true,
apply:a},
backgroundRepeat:{check:[b,
i,
j,
g,
f],
init:b,
apply:a},
backgroundColor:{check:k,
nullable:true,
apply:a}},
members:{init:function(n){n.useMarkup(this.getMarkup());
},
getMarkup:function(){if(this.__lr){return this.__lr;
}var o=qx.ui.decoration.Util.generateBackgroundMarkup(this.getBackgroundImage(),
this.getBackgroundRepeat(),
h);
return this.__lr=o;
},
resize:function(n,
p,
q){var r=n.getDomElement();
r.style.width=p+c;
r.style.height=q+c;
},
tint:function(n,
s){var t=qx.theme.manager.Color.getInstance();
var r=n.getDomElement();
if(s==null){s=this.getBackgroundColor();
}r.style.backgroundColor=t.resolve(s)||e;
},
__ls:{top:0,
right:0,
bottom:0,
left:0},
getInsets:function(){return this.__ls;
},
_applyStyle:function(){{};
}}});
})();
(function(){var a="",
b="==",
c=">",
d="between",
e="<",
f="regex",
g="!between",
h=">=",
j="!=",
k="<=",
l="font-weight",
m=";",
n="text-align",
o='g',
p=":",
q="qx.ui.table.cellrenderer.Conditional",
r="color",
s="font-style";
qx.Class.define(q,
{extend:qx.ui.table.cellrenderer.Default,
construct:function(t,
u,
v,
w){arguments.callee.base.call(this);
this.numericAllowed=[b,
j,
c,
e,
h,
k];
this.betweenAllowed=[d,
g];
this.conditions=[];
this.__lt=t||a;
this.__lu=u||a;
this.__lv=v||a;
this.__lw=w||a;
},
members:{__lt:null,
__lu:null,
__lv:null,
__lw:null,
__lx:function(x,
v){if(x[1]!=null){v[n]=x[1];
}
if(x[2]!=null){v[r]=x[2];
}
if(x[3]!=null){v[s]=x[3];
}
if(x[4]!=null){v[l]=x[4];
}},
addNumericCondition:function(x,
y,
t,
u,
v,
w,
z){var A=null;
if(qx.lang.Array.contains(this.numericAllowed,
x)){if(y!=null){A=[x,
t,
u,
v,
w,
y,
z];
}}
if(A!=null){this.conditions.push(A);
}else{throw new Error("Condition not recognized or value is null!");
}},
addBetweenCondition:function(x,
y,
B,
t,
u,
v,
w,
z){if(qx.lang.Array.contains(this.betweenAllowed,
x)){if(y!=null&&B!=null){var A=[x,
t,
u,
v,
w,
y,
B,
z];
}}
if(A!=null){this.conditions.push(A);
}else{throw new Error("Condition not recognized or value1/value2 is null!");
}},
addRegex:function(C,
t,
u,
v,
w,
z){if(C!=null){var A=[f,
t,
u,
v,
w,
C,
z];
}
if(A!=null){this.conditions.push(A);
}else{throw new Error("regex cannot be null!");
}},
_getCellStyle:function(D){if(!this.conditions.length){return D.style||a;
}var E=D.table.getTableModel();
var F;
var G;
var H;
var v={"text-align":this.__lt,
"color":this.__lu,
"font-style":this.__lv,
"font-weight":this.__lw};
for(F in this.conditions){G=false;
if(qx.lang.Array.contains(this.numericAllowed,
this.conditions[F][0])){if(this.conditions[F][6]==null){H=D.value;
}else{H=E.getValueById(this.conditions[F][6],
D.row);
}
switch(this.conditions[F][0]){case b:if(H==this.conditions[F][5]){G=true;
}break;
case j:if(H!=this.conditions[F][5]){G=true;
}break;
case c:if(H>this.conditions[F][5]){G=true;
}break;
case e:if(H<this.conditions[F][5]){G=true;
}break;
case h:if(H>=this.conditions[F][5]){G=true;
}break;
case k:if(H<=this.conditions[F][5]){G=true;
}break;
}}else if(qx.lang.Array.contains(this.betweenAllowed,
this.conditions[F][0])){if(this.conditions[F][7]==null){H=D.value;
}else{H=E.getValueById(this.conditions[F][7],
D.row);
}
switch(this.conditions[F][0]){case d:if(H>=this.conditions[F][5]&&H<=this.conditions[F][6]){G=true;
}break;
case g:if(H<this.conditions[F][5]&&H>this.conditions[F][6]){G=true;
}break;
}}else if(this.conditions[F][0]==f){if(this.conditions[F][6]==null){H=D.value;
}else{H=E.getValueById(this.conditions[F][6],
D.row);
}var I=new RegExp(this.conditions[F][5],
o);
G=I.test(H);
}if(G==true){this.__lx(this.conditions[F],
v);
}}var J=[];
for(var K in v){if(v[K]){J.push(K,
p,
v[K],
m);
}}return J.join(a);
}}});
})();
(function(){var a="qx.ui.table.cellrenderer.String",
b="qooxdoo-table-cell",
c="";
qx.Class.define(a,
{extend:qx.ui.table.cellrenderer.Conditional,
members:{_getContentHtml:function(d){return qx.bom.String.escape(d.value||c);
},
_getCellClass:function(d){return b;
}}});
})();
(function(){var a="",
b="org.argeo.slc.web.components.XmlRenderer",
c="yyyy-MM-dd HH:mm:ss",
d='param[@name="date"]',
e="MMM d, yy HH:mm:ss",
f=".",
g="Not Found",
h="param[@name='testName']";
qx.Class.define(b,
{extend:qx.ui.table.cellrenderer.String,
members:{_getContentHtml:function(i){var j=i.rowData;
if(!j)return a;
var k;
switch(i.col){case 0:k=h;
var l=org.argeo.slc.web.util.Element.selectSingleNode(j,
k);
var m=qx.bom.String.escape(qx.dom.Node.getText(l)||g);
break;
case 1:k=d;
var l=org.argeo.slc.web.util.Element.selectSingleNode(j,
k);
var m=qx.bom.String.escape(qx.dom.Node.getText(l)||0);
var n=m.split(f);
var o=new qx.util.format.DateFormat(c);
try{var p=o.parse(n[0]);
var q=new qx.util.format.DateFormat(e);
return q.format(p);
}catch(e){qx.log.Logger.info(e);
}break;
default:return a;
break;
}return m;
},
_getCellClass:function(i){return arguments.callee.base.call(this,
i);
}}});
})();
(function(){var a="auto",
b="/",
c="",
d="application/xml",
e="<div align=\"center\"><img src=\"resource/slc/dialog-ok.png\" height=\"16\" width=\"16\"></div>",
f="1*",
g="GET",
h="PASSED",
k="Id",
l="param[@name='uuid']",
m='"]',
n="Message",
o="slc:part-sub-list/slc:parts/slc:simple-result-part/slc:test-run-uuid",
p="http://argeo.org/projects/slc/schemas",
q="slc:part-sub-list/slc:parts/slc:simple-result-part/slc:message",
r="completed",
s="org.argeo.slc.web.components.Applet",
t="<div align=\"center\"><img src=\"resource/slc/flag.png\" height=\"16\" width=\"16\"></div>",
u="@path",
v='//slc:element[@path="',
w="slc:part-sub-list/slc:parts/slc:simple-result-part/slc:status",
x="State",
y="*/slc:label",
z="//slc:result-part",
A="../resultViewXml.xslt?uuid=",
B="Test";
qx.Class.define(s,
{extend:qx.ui.container.Composite,
construct:function(){arguments.callee.base.call(this);
this.setLayout(new qx.ui.layout.VBox());
this.passedStatus=e;
this.failedStatus=t;
},
properties:{},
members:{initData:function(C){this.data=C;
if(!C)return;
var D;
var E=qx.dom.Node.getText(org.argeo.slc.web.util.Element.selectSingleNode(this.data,
l));
D=A+E;
var F=org.argeo.slc.web.util.RequestManager.getInstance();
var G=F.getRequest(D,
g,
d);
G.addListener(r,
function(H){this.createXmlGui(H.getContent());
F.requestCompleted(G);
},
this);
G.send();
},
createXmlGui:function(I){var J={"slc":p};
qx.Class.include(qx.ui.treevirtual.TreeVirtual,
qx.ui.treevirtual.MNode);
this.tree=new qx.ui.treevirtual.TreeVirtual([B,
x,
n,
k]);
this.tree.getTableColumnModel().setDataCellRenderer(0,
new org.argeo.slc.web.util.TreeDataCellRenderer());
this.tree.setRowHeight(18);
this.tree.setStatusBarVisible(false);
var K=this.tree.getDataModel();
var L=org.argeo.slc.web.util.Element.selectNodes(I,
z,
J);
window.result=I;
var M={};
var N={};
for(var O=0;O<L.length;O++){var P=null;
var Q=L[O];
var R=qx.xml.Element.getSingleNodeText(Q,
u);
var S=R.split(b);
var T=c;
for(var U=0;U<S.length;U++){if(S[U]==c)continue;
T=T.concat(b,
S[U]);
if(N[T]){P=N[T];
continue;
}var V=org.argeo.slc.web.util.Element.selectSingleNode(I,
v+T+m,
J);
var W;
if(V!=null){W=org.argeo.slc.web.util.Element.getSingleNodeText(V,
y,
J);
}else{W=T;
}var X;
if(U<S.length-1){X=K.addBranch(P,
W,
false);
}else{X=K.addLeaf(P,
W);
K.setColumnData(X,
3,
org.argeo.slc.web.util.Element.getSingleNodeText(Q,
o,
J));
K.setColumnData(X,
2,
org.argeo.slc.web.util.Element.getSingleNodeText(Q,
q,
J));
var Y=org.argeo.slc.web.util.Element.getSingleNodeText(Q,
w,
J);
if(Y!=h){Y=this.failedStatus;
this._setParentBranchAsFailed(X);
}else{Y=this.passedStatus;
}K.setColumnData(X,
1,
Y);
}N[T]=X;
P=X;
}}this.add(this.tree,
{flex:1});
K.setData();
var ba=this.tree.getTableColumnModel();
var bb=ba.getBehavior();
bb.set(0,
{width:250,
minWidth:250});
bb.set(1,
{width:40});
bb.set(2,
{width:f});
bb.set(3,
{width:100});
ba.setDataCellRenderer(1,
new qx.ui.table.cellrenderer.Html());
},
_setParentBranchAsFailed:function(bc){var K=this.tree.getDataModel();
while(bc!=null&&bc!=0){var Q=this.tree.nodeGet(bc);
bc=Q.parentNodeId;
if(bc!=null&&bc!=0){K.setColumnData(bc,
1,
this.failedStatus);
this.tree.nodeSetOpened(bc,
true);
}}},
createHtmlGui:function(bd){var be=new qx.ui.embed.Html(bd);
be.setOverflowX(a);
be.setOverflowY(a);
this.add(be,
{flex:1});
}}});
})();
(function(){var a="dataChanged",
c="qx.event.type.DataEvent",
d="Left",
e="Right",
f="hidden",
g="Enter",
h="Boolean",
k="number",
l="changeSelection",
m="qx.ui.treevirtual.TreeVirtual",
n="string",
o="treevirtual",
p="object";
qx.Class.define(m,
{extend:qx.ui.table.Table,
construct:function(q,
r){if(!r){r={};
}
if(!r.dataModel){r.dataModel=new qx.ui.treevirtual.SimpleTreeDataModel();
}
if(r.treeColumn===undefined){r.treeColumn=0;
r.dataModel.setTreeColumn(r.treeColumn);
}
if(!r.treeDataCellRenderer){r.treeDataCellRenderer=new qx.ui.treevirtual.SimpleTreeDataCellRenderer();
}
if(!r.defaultDataCellRenderer){r.defaultDataCellRenderer=new qx.ui.treevirtual.DefaultDataCellRenderer();
}
if(!r.dataRowRenderer){r.dataRowRenderer=new qx.ui.treevirtual.SimpleTreeDataRowRenderer();
}
if(!r.selectionManager){r.selectionManager=function(t){return new qx.ui.treevirtual.SelectionManager(t);
};
}
if(!r.tableColumnModel){r.tableColumnModel=function(t){return new qx.ui.table.columnmodel.Resize(t);
};
}if(typeof (q)==n){q=[q];
}r.dataModel.setColumns(q);
r.dataModel.setTreeColumn(r.treeColumn);
r.dataModel.setTree(this);
arguments.callee.base.call(this,
r.dataModel,
r);
this.setColumnVisibilityButtonVisible(q.length>1);
this.setRowHeight(16);
this.setMetaColumnCounts(q.length>1?[1,
-1]:[1]);
this.setOverflow(f);
var u=r.treeDataCellRenderer;
var v=r.defaultDataCellRenderer;
var w=this.getTableColumnModel();
var x=this.getTableModel().getTreeColumn();
for(var y=0;y<q.length;y++){w.setDataCellRenderer(y,
y==x?u:v);
}this.setDataRowRenderer(r.dataRowRenderer);
this.setAlwaysUpdateCells(true);
this.setFocusCellOnMouseMove(true);
this.setShowCellFocusIndicator(false);
var z=this._getPaneScrollerArr();
for(var y=0;y<z.length;y++){z[y].setSelectBeforeFocus(true);
}},
events:{"treeOpenWithContent":c,
"treeOpenWhileEmpty":c,
"treeClose":c,
"changeSelection":c},
statics:{SelectionMode:{NONE:qx.ui.table.selection.Model.NO_SELECTION,
SINGLE:qx.ui.table.selection.Model.SINGLE_SELECTION,
SINGLE_INTERVAL:qx.ui.table.selection.Model.SINGLE_INTERVAL_SELECTION,
MULTIPLE_INTERVAL:qx.ui.table.selection.Model.MULTIPLE_INTERVAL_SELECTION,
MULTIPLE_INTERVAL_TOGGLE:qx.ui.table.selection.Model.MULTIPLE_INTERVAL_SELECTION_TOGGLE}},
properties:{openCloseClickSelectsRow:{check:h,
init:false},
appearance:{refine:true,
init:o}},
members:{getDataModel:function(){return this.getTableModel();
},
setUseTreeLines:function(A){var B=this.getTableModel();
var x=B.getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
C.setUseTreeLines(A);
if(B.hasListener(a)){var D={firstRow:0,
lastRow:B._rowArr.length-1,
firstColumn:0,
lastColumn:B.getColumnCount()-1};
B.fireDataEvent(a,
D);
}},
getUseTreeLines:function(){var x=this.getTableModel().getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
return C.getUseTreeLines();
},
setAlwaysShowOpenCloseSymbol:function(A){var B=this.getTableModel();
var x=B.getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
C.setAlwaysShowOpenCloseSymbol(A);
if(B.hasListener(a)){var D={firstRow:0,
lastRow:B._rowArr.length-1,
firstColumn:0,
lastColumn:B.getColumnCount()-1};
B.fireDataEvent(a,
D);
}},
setExcludeFirstLevelTreeLines:function(A){var B=this.getTableModel();
var x=B.getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
C.setExcludeFirstLevelTreeLines(A);
if(B.hasListener(a)){var D={firstRow:0,
lastRow:B._rowArr.length-1,
firstColumn:0,
lastColumn:B.getColumnCount()-1};
B.fireDataEvent(a,
D);
}},
getExcludeFirstLevelTreeLines:function(){var x=this.getTableModel().getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
return C.getExcludeFirstLevelTreeLines();
},
getAlwaysShowOpenCloseSymbol:function(){var x=this.getTableModel().getTreeColumn();
var C=this.getTableColumnModel().getDataCellRenderer(x);
return C.getAlwaysShowOpenCloseSymbol();
},
setSelectionMode:function(E){this.getSelectionModel().setSelectionMode(E);
},
getSelectionMode:function(E){return this.getSelectionModel().getSelectionMode();
},
setCellFocusAttributes:function(F){if(!F.opacity){F.opacity=0.2;
}var z=this._getPaneScrollerArr();
for(var y=0;y<z.length;y++){}},
getHierarchy:function(G){var _this=this;
var H=[];
var I,
J;
if(typeof (G)==p){J=G;
I=J.nodeId;
}else if(typeof (G)==k){I=G;
}else{throw new Error("Expected node object or node id");
}function K(I){if(!I){return ;
}var J=_this.getTableModel().getData()[I];
H.unshift(J.label);
K(J.parentNodeId);
}K(I);
return H;
},
getSelectedNodes:function(){return this.getTableModel().getSelectedNodes();
},
_onKeyPress:function(L){if(!this.getEnabled()){return;
}var M=L.getKeyIdentifier();
var N=false;
var O=L.getModifiers();
if(O==0){switch(M){case g:var P=this.getTableModel();
var Q=this.getFocusedColumn();
var x=P.getTreeColumn();
if(Q==x){var R=this.getFocusedRow();
var J=P.getValue(x,
R);
if(!J.bHideOpenClose){P.setState(J,
{bOpened:!J.bOpened});
}N=true;
}break;
case d:this.moveFocusedCell(-1,
0);
break;
case e:this.moveFocusedCell(1,
0);
break;
}}else if(O==qx.event.type.Dom.CTRL_MASK){switch(M){case d:var P=this.getTableModel();
var R=this.getFocusedRow();
var x=P.getTreeColumn();
var J=P.getValue(x,
R);
if((J.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH)&&!J.bHideOpenClose&&J.bOpened){P.setState(J,
{bOpened:!J.bOpened});
}this.setFocusedCell(x,
R,
true);
N=true;
break;
case e:var P=this.getTableModel();
var R=this.getFocusedRow();
var x=P.getTreeColumn();
var J=P.getValue(x,
R);
if((J.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH)&&!J.bHideOpenClose&&!J.bOpened){P.setState(J,
{bOpened:!J.bOpened});
}this.setFocusedCell(x,
R,
true);
N=true;
break;
}}else if(O==qx.event.type.Dom.SHIFT_MASK){switch(M){case d:var P=this.getTableModel();
var R=this.getFocusedRow();
var x=P.getTreeColumn();
var J=P.getValue(x,
R);
if(J.parentNodeId){var S=P.getRowFromNode(J.parentNodeId);
this.setFocusedCell(this._focusedCol,
S,
true);
}N=true;
break;
case e:var P=this.getTableModel();
var R=this.getFocusedRow();
var x=P.getTreeColumn();
var J=P.getValue(x,
R);
if((J.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH)&&!J.bHideOpenClose){if(!J.bOpened){P.setState(J,
{bOpened:!J.bOpened});
}if(J.children.length>0){this.moveFocusedCell(0,
1);
}}N=true;
break;
}}if(N){L.preventDefault();
L.stopPropagation();
}else{arguments.callee.base.call(this,
L);
}},
_onkeypress:function(L){if(!this.getEnabled()){return;
}var N=false;
var M=L.getKeyIdentifier();
switch(M){case d:case e:N=true;
break;
}
if(N){L.preventDefault();
L.stopPropagation();
}else{arguments.callee.base.call(this,
L);
}},
_onSelectionChanged:function(L){this.getTableModel()._clearSelections();
if(this.getSelectionMode()!=qx.ui.treevirtual.TreeVirtual.SelectionMode.NONE){var T=this._calculateSelectedNodes();
this.fireDataEvent(l,
T);
}arguments.callee.base.call(this,
L);
},
_calculateSelectedNodes:function(){var B=this.getTableModel();
var U=this.getSelectionModel().getSelectedRanges();
var T=[];
var J;
for(var y=0;y<U.length;y++){for(var V=U[y].minIndex;V<=U[y].maxIndex;V++){J=B.getValue(B.getTreeColumn(),
V);
B.setState(J,
{bSelected:true});
T.push(J);
}}return T;
},
setOverflow:function(W){if(W!=f){throw new Error("Tree overflow must be hidden.  "+"The internal elements of it will scroll.");
}}}});
})();
(function(){var a="dataChanged",
b="number",
c="object",
d="treeOpenWhileEmpty",
e="treeOpenWithContent",
f="_treeColumn",
g="_selections",
h="bSelected",
k="__ly",
l="_nodeRowMap",
m="qx.ui.treevirtual.SimpleTreeDataModel",
n="bOpened",
o="_rowArr",
p="_nodeArr",
q="treeClose",
r="<virtual root>";
qx.Class.define(m,
{extend:qx.ui.table.model.Simple,
construct:function(){arguments.callee.base.call(this);
this._rowArr=[];
this._nodeArr=[];
this._nodeRowMap=[];
this._treeColumn=0;
this._selections={};
this._nodeArr.push(arguments.callee.self.__lz());
},
statics:{__ly:null,
__lz:function(){return {label:r,
nodeId:0,
bOpened:true,
children:[]};
},
Type:{LEAF:1,
BRANCH:2}},
members:{setTree:function(s){this.__ly=s;
},
getTree:function(){return this.__ly;
},
setColumnEditable:function(t,
u){arguments.callee.base.call(this,
t,
u);
},
isColumnSortable:function(t){return false;
},
sortByColumn:function(t,
v){throw new Error("Trees can not be sorted by column");
},
getSortColumnIndex:function(){return -1;
},
setTreeColumn:function(t){this._treeColumn=t;
},
getTreeColumn:function(){return this._treeColumn;
},
getRowCount:function(){return this._rowArr.length;
},
getRowData:function(w){return this._rowArr[w];
},
getValue:function(t,
w){if(w<0||w>=this._rowArr.length){throw new Error("this._rowArr row "+"("+w+") out of bounds: "+this._rowArr+" (0.."+(this._rowArr.length-1)+")");
}
if(t<0||t>=this._rowArr[w].length){throw new Error("this._rowArr column "+"("+t+") out of bounds: "+this._rowArr[w]+" (0.."+(this._rowArr[w].length-1)+")");
}return this._rowArr[w][t];
},
setValue:function(t,
w,
x){if(t==this._treeColumn){return ;
}var y=this.getNodeFromRow(w);
if(y.columnData[t]!=x){y.columnData[t]=x;
this.setData();
if(this.hasListener(a)){var z={firstRow:y.nodeId,
lastRow:y.nodeId,
firstColumn:t,
lastColumn:t};
this.fireDataEvent(a,
z);
}}},
_addNode:function(A,
B,
C,
D,
E,
F,
G){var H;
if(A){H=this._nodeArr[A];
if(!H){throw new Error("Request to add a child to a non-existent parent");
}if(H.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF){throw new Error("Sorry, a LEAF may not have children.");
}}else{H=this._nodeArr[0];
A=0;
}if(E==qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF){C=false;
D=false;
}var I=this._nodeArr.length;
var y={type:E,
nodeId:I,
parentNodeId:A,
label:B,
bSelected:false,
bOpened:C,
bHideOpenClose:D,
icon:F,
iconSelected:G,
children:[],
columnData:[]};
this._nodeArr.push(y);
H.children.push(I);
return I;
},
addBranch:function(A,
B,
C,
D,
F,
G){return this._addNode(A,
B,
C,
D,
qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH,
F,
G);
},
addLeaf:function(A,
B,
F,
G){return this._addNode(A,
B,
false,
false,
qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF,
F,
G);
},
prune:function(J,
K){var I;
if(typeof (J)==c){y=J;
I=y.nodeId;
}else if(typeof (J)==b){I=J;
}else{throw new Error("Expected node object or node id");
}for(var L=this._nodeArr[I].children.length-1;L>=0;L--){this.prune(this._nodeArr[I].children[L],
true);
}if(K&&I!=0){var y=this._nodeArr[I];
qx.lang.Array.remove(this._nodeArr[y.parentNodeId].children,
I);
if(this._selections[I]){delete this._selections[I];
}this._nodeArr[I]=null;
}},
setData:function(M){var _this=this;
function N(){var O=function(I,
P){var Q=null;
var R;
var S=_this._nodeArr[I].children.length;
for(var L=0;L<S;L++){R=_this._nodeArr[I].children[L];
Q=_this._nodeArr[R];
if(Q==null){continue;
}Q.level=P;
Q.bFirstChild=(L==0);
Q.lastChild=[L==S-1];
var T=_this._nodeArr[Q.parentNodeId];
while(T.nodeId){var U=T.lastChild[T.lastChild.length-1];
Q.lastChild.unshift(U);
T=_this._nodeArr[T.parentNodeId];
}if(!Q.columnData){Q.columnData=[];
}
if(Q.columnData.length<_this.getColumnCount()){Q.columnData[_this.getColumnCount()-1]=null;
}var V=[];
if(Q.columnData){for(var W=0;W<Q.columnData.length;W++){if(W==_this._treeColumn){V.push(Q);
}else{V.push(Q.columnData[W]);
}}}else{V.push(Q);
}if(Q.bSelected){V.selected=true;
}_this._nodeRowMap[Q.nodeId]=_this._rowArr.length;
_this._rowArr.push(V);
if(Q.bOpened){O(R,
P+1);
}}};
_this._rowArr=[];
_this._nodeRowMap=[];
O(0,
1);
if(_this.hasListener(a)){var z={firstRow:0,
lastRow:_this._rowArr.length-1,
firstColumn:0,
lastColumn:_this.getColumnCount()-1};
_this.fireDataEvent(a,
z);
}}
if(M instanceof Array){for(var L=0;L<M.length;L++){if(M[L].selected){this._selections[L]=true;
}}this._nodeArr=M;
}else if(M!==null&&M!==undefined){throw new Error("Expected array of node objects or null/undefined; "+"got "+typeof (M));
}N();
},
getData:function(){return this._nodeArr;
},
clearData:function(){this._clearSelections();
this.setData([arguments.callee.self.__lz()]);
},
setColumnData:function(I,
t,
z){this._nodeArr[I].columnData[t]=z;
},
getColumnData:function(I,
t){return this._nodeArr[I].columnData[t];
},
setState:function(J,
X){var I,
y;
if(typeof (J)==c){y=J;
I=y.nodeId;
}else if(typeof (J)==b){I=J;
y=this._nodeArr[I];
}else{throw new Error("Expected node object or node id");
}
for(var Y in X){switch(Y){case h:var ba=this.getRowFromNodeId(I);
var bb=this.getTree().getSelectionModel();
var bc=qx.ui.treevirtual.TreeVirtual;
var bd=(typeof (ba)===b&&this.getTree().getSelectionMode()!=bc.SelectionMode.NONE);
if(X[Y]){this._selections[I]=true;
if(bd&&!bb.isSelectedIndex(ba)){bb.setSelectionInterval(ba,
ba);
}}else{delete this._selections[I];
if(bd&&bb.isSelectedIndex(ba)){bb.removeSelectionInterval(ba,
ba);
}}break;
case n:if(X[Y]==y.bOpened){break;
}var s=this.__ly;
if(y.bOpened){s.fireDataEvent(q,
y);
}else{if(y.children.length>0){s.fireDataEvent(e,
y);
}else{s.fireDataEvent(d,
y);
}}if(!y.bHideOpenClose){y.bOpened=!y.bOpened;
s.getSelectionModel()._clearSelection();
}this.setData();
break;
default:break;
}y[Y]=X[Y];
}},
getNodeRowMap:function(){return this._nodeRowMap;
},
getRowFromNodeId:function(I){return this._nodeRowMap[I];
},
getNodeFromRow:function(w){return this._nodeArr[this._rowArr[w][this._treeColumn].nodeId];
},
_clearSelections:function(){for(var be in this._selections){this._nodeArr[be].bSelected=false;
}this._selections={};
},
getSelectedNodes:function(){var bf=[];
for(var I in this._selections){bf.push(this._nodeArr[I]);
}return bf;
}},
destruct:function(){this._disposeFields(o,
p,
l,
f,
g,
k);
}});
})();
(function(){var a="#CCCCCC",
b="#F3F3F3",
c="#E4E4E4",
d="#1a1a1a",
e="#084FAB",
f="gray",
g="#fffefe",
h="white",
i="#4a4a4a",
j="#EEEEEE",
k="#80B4EF",
l="#ffffdd",
m="#334866",
n="#00204D",
o="#666666",
p="#99C3FE",
q="#808080",
r="#F4F4F4",
s="#001533",
t="#909090",
u="#FCFCFC",
v="#314a6e",
w="#0880EF",
x="#4d4d4d",
y="#DFDFDF",
z="#000000",
A="#26364D",
B="#6B6A6E",
C="#AFAFAF",
D="#404955",
E="#AAAAAA",
F="qx.theme.modern.Color";
qx.Theme.define(F,
{colors:{"background-application":y,
"background-pane":b,
"background-light":u,
"background-medium":j,
"background-splitpane":C,
"background-tip":l,
"background-odd":c,
"text-light":t,
"text-gray":i,
"text-label":d,
"text-title":v,
"text-input":z,
"text-hovered":s,
"text-disabled":B,
"text-selected":g,
"text-active":A,
"text-inactive":D,
"border-main":x,
"border-separator":q,
"border-input":m,
"border-pane":n,
"border-button":o,
"border-column":a,
"border-focused":p,
"table-pane":b,
"table-focus-indicator":w,
"table-row-background-focused-selected":e,
"table-row-background-focused":k,
"table-row-background-selected":e,
"table-row-background-even":b,
"table-row-background-odd":c,
"table-row-selected":g,
"table-row":d,
"table-row-line":a,
"table-column-line":a,
"progressive-table-header":E,
"progressive-table-row-background-even":r,
"progressive-table-row-background-odd":c,
"progressive-progressbar-background":f,
"progressive-progressbar-indicator-done":a,
"progressive-progressbar-indicator-undone":h,
"progressive-progressbar-percent-background":f,
"progressive-progressbar-percent-text":h}});
})();
(function(){var a="_applyStyle",
b="repeat",
c="px",
d="scale",
e="solid",
f="Color",
g="double",
h="px ",
i="position:absolute;top:0;left:0;",
j="dotted",
k="_applyWidth",
l="qx.ui.decoration.Uniform",
m="repeat-y",
n="String",
o="",
p="__lA",
q="PositiveInteger",
r="border:",
s="dashed",
t="__lB",
u="no-repeat",
v=" ",
w="repeat-x",
x=";";
qx.Class.define(l,
{extend:qx.core.Object,
implement:[qx.ui.decoration.IDecorator],
construct:function(y,
z,
A){arguments.callee.base.call(this);
if(y!=null){this.setWidth(y);
}
if(z!=null){this.setStyle(z);
}
if(A!=null){this.setColor(A);
}},
properties:{width:{check:q,
init:0,
apply:k},
style:{nullable:true,
check:[e,
j,
s,
g],
init:e,
apply:a},
color:{nullable:true,
check:f,
apply:a},
backgroundImage:{check:n,
nullable:true,
apply:a},
backgroundRepeat:{check:[b,
w,
m,
u,
d],
init:b,
apply:a},
backgroundColor:{check:f,
nullable:true,
apply:a}},
members:{init:function(B){B.useMarkup(this.getMarkup());
},
getMarkup:function(){if(this.__lA){return this.__lA;
}var C=i;
var y=this.getWidth();
{};
var D=qx.theme.manager.Color.getInstance();
C+=r+y+h+this.getStyle()+v+D.resolve(this.getColor())+x;
var E=qx.ui.decoration.Util.generateBackgroundMarkup(this.getBackgroundImage(),
this.getBackgroundRepeat(),
C);
return this.__lA=E;
},
resize:function(B,
y,
F){var G=this.getBackgroundImage()&&this.getBackgroundRepeat()==d;
if(G||qx.bom.client.Feature.CONTENT_BOX){var H=this.getWidth()*2;
y-=H;
F-=H;
if(y<0){y=0;
}
if(F<0){F=0;
}}var I=B.getDomElement();
I.style.width=y+c;
I.style.height=F+c;
},
tint:function(B,
J){var D=qx.theme.manager.Color.getInstance();
var I=B.getDomElement();
if(J==null){J=this.getBackgroundColor();
}I.style.backgroundColor=D.resolve(J)||o;
},
getInsets:function(){if(this.__lB){return this.__lB;
}var y=this.getWidth();
this.__lB={top:y,
right:y,
bottom:y,
left:y};
return this.__lB;
},
_applyWidth:function(){{};
this.__lB=null;
},
_applyStyle:function(){{};
}},
destruct:function(){this._disposeFields(p,
t);
}});
})();
(function(){var a="px",
b="_applyInsets",
c="Number",
d="no-repeat",
e="scale-x",
f="scale-y",
g="-tr",
h="-l",
i="insetTop",
j='</div>',
k="insetBottom",
l="scale",
m="-br",
n="__lD",
o="-t",
p="-tl",
q="-r",
r='<div style="position:absolute;top:0;left:0;overflow:hidden;font-size:0;line-height:0;">',
s="_applyBaseImage",
t="-b",
u="shorthand",
v="String",
w="insetRight",
x="",
y="-bl",
z="__lE",
A="__lC",
B="-c",
C="__lF",
D="insetLeft",
E="qx.ui.decoration.Grid";
qx.Class.define(E,
{extend:qx.core.Object,
implement:[qx.ui.decoration.IDecorator],
construct:function(F,
G){arguments.callee.base.call(this);
if(F!=null){this.setBaseImage(F);
}
if(G!=null){this.setInsets(G);
}},
properties:{baseImage:{check:v,
nullable:true,
apply:s},
insetLeft:{check:c,
init:0,
apply:b},
insetRight:{check:c,
init:0,
apply:b},
insetBottom:{check:c,
init:0,
apply:b},
insetTop:{check:c,
init:0,
apply:b},
insets:{group:[i,
w,
k,
D],
mode:u}},
members:{__lC:null,
__lD:null,
__lE:null,
__lF:null,
init:function(H){H.useMarkup(this.getMarkup());
},
getMarkup:function(){if(this.__lC){return this.__lC;
}var I=qx.bom.element.Decoration;
var J=this.__lE;
var K=this.__lF;
var L=[];
L.push(r);
L.push(I.create(J.tl,
d,
{top:0,
left:0}));
L.push(I.create(J.t,
e,
{top:0,
left:K.left+a}));
L.push(I.create(J.tr,
d,
{top:0,
right:0}));
L.push(I.create(J.bl,
d,
{bottom:0,
left:0}));
L.push(I.create(J.b,
e,
{bottom:0,
left:K.left+a}));
L.push(I.create(J.br,
d,
{bottom:0,
right:0}));
L.push(I.create(J.l,
f,
{top:K.top+a,
left:0}));
L.push(I.create(J.c,
l,
{top:K.top+a,
left:K.left+a}));
L.push(I.create(J.r,
f,
{top:K.top+a,
right:0}));
L.push(j);
return this.__lC=L.join(x);
},
resize:function(H,
M,
N){var K=this.__lF;
var O=M-K.left-K.right;
var P=N-K.top-K.bottom;
var Q=H.getDomElement();
Q.style.width=M+a;
Q.style.height=N+a;
Q.childNodes[1].style.width=O+a;
Q.childNodes[4].style.width=O+a;
Q.childNodes[7].style.width=O+a;
Q.childNodes[6].style.height=P+a;
Q.childNodes[7].style.height=P+a;
Q.childNodes[8].style.height=P+a;
},
tint:function(H,
R){},
getInsets:function(){if(this.__lD){return this.__lD;
}return this.__lD={left:this.getInsetLeft(),
right:this.getInsetRight(),
bottom:this.getInsetBottom(),
top:this.getInsetTop()};
},
_applyInsets:function(){{};
this.__lD=null;
},
_applyBaseImage:function(S,
T){{};
var U=qx.util.ResourceManager;
if(S){var V=qx.util.AliasManager.getInstance();
var W=V.resolve(S);
var X=/(.*)(\.[a-z]+)$/.exec(W);
var Y=X[1];
var ba=X[2];
var J=this.__lE={tl:Y+p+ba,
t:Y+o+ba,
tr:Y+g+ba,
bl:Y+y+ba,
b:Y+t+ba,
br:Y+m+ba,
l:Y+h+ba,
c:Y+B+ba,
r:Y+q+ba};
this.__lF={top:U.getImageHeight(J.t),
bottom:U.getImageHeight(J.b),
left:U.getImageWidth(J.l),
right:U.getImageWidth(J.r)};
}}},
destruct:function(){this._disposeFields(A,
n,
z,
C);
}});
})();
(function(){var a="_applyStyle",
b='"></div>',
c="Color",
d="repeat",
e='<div style="',
f='border:',
g="1px solid ",
h="",
i=";",
j="px",
k="position:absolute;top:1px;left:1px;",
l="qx.ui.decoration.Beveled",
m="scale",
n='<div style="position:absolute;top:1px;left:0px;',
o='<div style="position:absolute;top:1px;left:1px;',
p="repeat-y",
q='border-bottom:',
r="String",
s='border-right:',
t='</div>',
u='border-top:',
v="Number",
w="no-repeat",
x='position:absolute;top:0px;left:1px;',
y="repeat-x",
z='<div style="overflow:hidden;font-size:0;line-height:0;">',
A='border-left:';
qx.Class.define(l,
{extend:qx.core.Object,
implement:[qx.ui.decoration.IDecorator],
construct:function(B,
C,
D){arguments.callee.base.call(this);
if(B!=null){this.setOuterColor(B);
}
if(C!=null){this.setInnerColor(C);
}
if(D!=null){this.setInnerOpacity(D);
}},
properties:{innerColor:{check:c,
nullable:true,
apply:a},
innerOpacity:{check:v,
init:1,
apply:a},
outerColor:{check:c,
nullable:true,
apply:a},
backgroundImage:{check:r,
nullable:true,
apply:a},
backgroundRepeat:{check:[d,
y,
p,
w,
m],
init:d,
apply:a},
backgroundColor:{check:c,
nullable:true,
apply:a}},
members:{__lG:null,
_applyStyle:function(){{};
},
init:function(E){E.useMarkup(this.getMarkup());
},
getMarkup:function(){if(this.__lG){return this.__lG;
}var F=qx.theme.manager.Color.getInstance();
var G=[];
var H=g+F.resolve(this.getOuterColor())+i;
var I=g+F.resolve(this.getInnerColor())+i;
G.push(z);
G.push(e);
G.push(f,
H);
G.push(qx.bom.element.Opacity.compile(0.35));
G.push(b);
G.push(n);
G.push(A,
H);
G.push(s,
H);
G.push(b);
G.push(e);
G.push(x);
G.push(u,
H);
G.push(q,
H);
G.push(b);
var J=k;
G.push(qx.ui.decoration.Util.generateBackgroundMarkup(this.getBackgroundImage(),
this.getBackgroundRepeat(),
J));
G.push(o);
G.push(f,
I);
G.push(qx.bom.element.Opacity.compile(this.getInnerOpacity()));
G.push(b);
G.push(t);
return this.__lG=G.join(h);
},
resize:function(E,
K,
L){if(K<4){K=4;
}
if(L<4){L=4;
}if(qx.bom.client.Feature.CONTENT_BOX){var M=K-2;
var N=L-2;
var O=M;
var P=N;
var Q=K-4;
var R=L-4;
}else{var M=K;
var N=L;
var O=K-2;
var P=L-2;
var Q=O;
var R=P;
}var S=E.getDomElement();
var T=j;
var U=S.childNodes[0].style;
U.width=M+T;
U.height=N+T;
var V=S.childNodes[1].style;
V.width=M+T;
V.height=P+T;
var W=S.childNodes[2].style;
W.width=O+T;
W.height=N+T;
var X=S.childNodes[3].style;
X.width=O+T;
X.height=P+T;
var Y=S.childNodes[4].style;
Y.width=Q+T;
Y.height=R+T;
},
tint:function(E,
ba){var S=E.getDomElement();
var F=qx.theme.manager.Color.getInstance();
if(ba==null){ba=this.getBackgroundColor();
}S.childNodes[3].style.backgroundColor=F.resolve(ba)||h;
},
getInsets:function(){return this.__lH;
},
__lH:{top:2,
right:2,
bottom:2,
left:2}}});
})();
(function(){var a="solid",
b="scale",
c="border-main",
d="border-separator",
e="white",
f="decoration/table/header-cell.png",
g="repeat-x",
h="#f8f8f8",
i="#b6b6b6",
j="background-pane",
k="repeat-y",
l="border-input",
m="background-light",
n="decoration/form/input.png",
o="decoration/tabview/tab-button-top-active.png",
p="decoration/form/button-c.png",
q="decoration/scrollbar/scrollbar-bg-vertical.png",
r="decoration/shadow/shadow-small.png",
s="decoration/form/button-checked.png",
t="decoration/tabview/tab-button-left-inactive.png",
u="decoration/groupbox/groupbox.png",
v="#FAFAFA",
w="decoration/pane/pane.png",
x="decoration/menu/background.png",
y="decoration/toolbar/toolbar-part.png",
z="decoration/window/captionbar-inactive.png",
A="decoration/tabview/tab-button-top-inactive.png",
B="decoration/menu/bar-background.png",
C="decoration/tabview/tab-button-bottom-active.png",
D="decoration/form/button-hovered.png",
E="#b8b8b8",
F="qx/decoration/Modern",
G="decoration/window/statusbar.png",
H="border-focused",
I="decoration/selection.png",
J="table-focus-indicator",
K="#F2F2F2",
L="decoration/form/button-checked-c.png",
M="decoration/scrollbar/scrollbar-bg-horizontal.png",
N="qx.theme.modern.Decoration",
O="#f4f4f4",
P="decoration/form/button.png",
Q="decoration/app-header.png",
R="decoration/tabview/tabview-pane.png",
S="decoration/form/button-focused.png",
T="decoration/tabview/tab-button-bottom-inactive.png",
U="decoration/window/captionbar-active.png",
V="decoration/tabview/tab-button-right-active.png",
W="decoration/form/button-pressed.png",
X="decoration/scrollbar/scrollbar-button-bg-horizontal.png",
Y="decoration/tabview/tab-button-left-active.png",
ba="background-splitpane",
bb="decoration/form/button-checked-focused.png",
bc="#C5C5C5",
bd="decoration/toolbar/toolbar-gradient.png",
be="decoration/tabview/tab-button-right-inactive.png",
bf="decoration/scrollbar/scrollbar-button-bg-vertical.png",
bg="decoration/shadow/shadow.png";
qx.Theme.define(N,
{resource:F,
decorations:{"main":{decorator:qx.ui.decoration.Uniform,
style:{width:1,
color:c}},
"selected":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:I,
backgroundRepeat:b}},
"pane":{decorator:qx.ui.decoration.Grid,
style:{baseImage:w,
insets:[0,
2,
3,
0]}},
"group":{decorator:qx.ui.decoration.Grid,
style:{baseImage:u}},
"separator-horizontal":{decorator:qx.ui.decoration.Single,
style:{widthLeft:1,
colorLeft:d}},
"separator-vertical":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:d}},
"shadow-window":{decorator:qx.ui.decoration.Grid,
style:{baseImage:bg,
insets:[4,
8,
8,
4]}},
"shadow-popup":{decorator:qx.ui.decoration.Grid,
style:{baseImage:r,
insets:[0,
3,
3,
0]}},
"scrollbar-horizontal":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:M,
backgroundRepeat:g}},
"scrollbar-vertical":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:q,
backgroundRepeat:k}},
"scrollbar-slider-horizontal":{decorator:qx.ui.decoration.Beveled,
style:{backgroundImage:X,
backgroundRepeat:b,
outerColor:c,
innerColor:e,
innerOpacity:0.5}},
"scrollbar-slider-vertical":{decorator:qx.ui.decoration.Beveled,
style:{backgroundImage:bf,
backgroundRepeat:b,
outerColor:c,
innerColor:e,
innerOpacity:0.5}},
"button":{decorator:qx.ui.decoration.Grid,
style:{baseImage:P,
insets:2}},
"button-focused":{decorator:qx.ui.decoration.Grid,
style:{baseImage:S,
insets:2}},
"button-hovered":{decorator:qx.ui.decoration.Grid,
style:{baseImage:D,
insets:2}},
"button-pressed":{decorator:qx.ui.decoration.Grid,
style:{baseImage:W,
insets:2}},
"button-checked":{decorator:qx.ui.decoration.Grid,
style:{baseImage:s,
insets:2}},
"button-checked-focused":{decorator:qx.ui.decoration.Grid,
style:{baseImage:bb,
insets:2}},
"input":{decorator:qx.ui.decoration.Beveled,
style:{outerColor:l,
innerColor:e,
innerOpacity:0.5,
backgroundImage:n,
backgroundRepeat:g,
backgroundColor:m}},
"input-focused":{decorator:qx.ui.decoration.Beveled,
style:{outerColor:l,
innerColor:H,
backgroundImage:n,
backgroundRepeat:g,
backgroundColor:m}},
"toolbar":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:bd,
backgroundRepeat:b}},
"toolbar-button-hovered":{decorator:qx.ui.decoration.Beveled,
style:{outerColor:i,
innerColor:h,
backgroundImage:p,
backgroundRepeat:b}},
"toolbar-button-checked":{decorator:qx.ui.decoration.Beveled,
style:{outerColor:i,
innerColor:h,
backgroundImage:L,
backgroundRepeat:b}},
"toolbar-separator":{decorator:qx.ui.decoration.Single,
style:{widthLeft:1,
widthRight:1,
colorLeft:E,
colorRight:O,
styleLeft:a,
styleRight:a}},
"toolbar-part":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:y,
backgroundRepeat:k}},
"tabview-pane":{decorator:qx.ui.decoration.Grid,
style:{baseImage:R,
insets:[0,
2,
3,
0]}},
"tabview-page-button-top-active":{decorator:qx.ui.decoration.Grid,
style:{baseImage:o}},
"tabview-page-button-top-inactive":{decorator:qx.ui.decoration.Grid,
style:{baseImage:A}},
"tabview-page-button-bottom-active":{decorator:qx.ui.decoration.Grid,
style:{baseImage:C}},
"tabview-page-button-bottom-inactive":{decorator:qx.ui.decoration.Grid,
style:{baseImage:T}},
"tabview-page-button-left-active":{decorator:qx.ui.decoration.Grid,
style:{baseImage:Y}},
"tabview-page-button-left-inactive":{decorator:qx.ui.decoration.Grid,
style:{baseImage:t}},
"tabview-page-button-right-active":{decorator:qx.ui.decoration.Grid,
style:{baseImage:V}},
"tabview-page-button-right-inactive":{decorator:qx.ui.decoration.Grid,
style:{baseImage:be}},
"splitpane":{decorator:qx.ui.decoration.Uniform,
style:{backgroundColor:j,
width:3,
color:ba,
style:a}},
"window":{decorator:qx.ui.decoration.Single,
style:{backgroundColor:j,
width:1,
color:c,
widthTop:0}},
"window-captionbar-active":{decorator:qx.ui.decoration.Grid,
style:{baseImage:U}},
"window-captionbar-inactive":{decorator:qx.ui.decoration.Grid,
style:{baseImage:z}},
"window-statusbar":{decorator:qx.ui.decoration.Grid,
style:{baseImage:G}},
"table":{decorator:qx.ui.decoration.Single,
style:{width:1,
color:c,
style:a}},
"table-statusbar":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:c,
style:a}},
"table-scroller-header":{decorator:qx.ui.decoration.Single,
style:{backgroundImage:f,
backgroundRepeat:b,
widthBottom:1,
colorBottom:c,
style:a}},
"table-header-cell":{decorator:qx.ui.decoration.Single,
style:{widthRight:1,
colorRight:d,
styleRight:a}},
"table-header-cell-hovered":{decorator:qx.ui.decoration.Single,
style:{widthRight:1,
colorRight:d,
styleRight:a,
widthBottom:1,
colorBottom:e,
styleBottom:a}},
"table-column-button":{decorator:qx.ui.decoration.Single,
style:{backgroundImage:f,
backgroundRepeat:b,
widthBottom:1,
colorBottom:c,
style:a}},
"table-scroller-focus-indicator":{decorator:qx.ui.decoration.Single,
style:{width:2,
color:J,
style:a}},
"progressive-table-header":{decorator:qx.ui.decoration.Single,
style:{width:1,
color:c,
style:a}},
"progressive-table-header-cell":{decorator:qx.ui.decoration.Single,
style:{backgroundImage:f,
backgroundRepeat:b,
widthRight:1,
colorRight:K,
style:a}},
"menu":{decorator:qx.ui.decoration.Single,
style:{backgroundImage:x,
backgroundRepeat:b,
width:1,
color:c,
style:a}},
"menu-separator":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:bc,
widthBottom:1,
colorBottom:v}},
"menubar":{decorator:qx.ui.decoration.Single,
style:{backgroundImage:B,
backgroundRepeat:b,
width:1,
color:d,
style:a}},
"app-header":{decorator:qx.ui.decoration.Background,
style:{backgroundImage:Q,
backgroundRepeat:b}}}});
})();
(function(){var a="win98",
b="osx2",
c="osx0",
d="osx4",
e="win95",
f="win2000",
g="osx1",
h="osx5",
i="osx3",
j="Windows NT 5.01",
k=")",
l="winxp",
m="freebsd",
n="sunos",
o="SV1",
p="|",
q="nintendods",
r="winnt4",
s="wince",
t="winme",
u="os9",
v="\.",
w="osx",
x="linux",
y="netbsd",
z="winvista",
A="(",
B="win2003",
C="symbian",
D="g",
E="qx.bom.client.System",
F=" Mobile/";
qx.Bootstrap.define(E,
{statics:{NAME:"",
SP1:false,
SP2:false,
WIN95:false,
WIN98:false,
WINME:false,
WINNT4:false,
WIN2000:false,
WINXP:false,
WIN2003:false,
WINVISTA:false,
WINCE:false,
LINUX:false,
SUNOS:false,
FREEBSD:false,
NETBSD:false,
OSX:false,
OS9:false,
SYMBIAN:false,
NINTENDODS:false,
PSP:false,
IPHONE:false,
__lI:{"Windows NT 6.0":z,
"Windows NT 5.2":B,
"Windows NT 5.1":l,
"Windows NT 5.0":f,
"Windows 2000":f,
"Windows NT 4.0":r,
"Win 9x 4.90":t,
"Windows CE":s,
"Windows 98":a,
"Win98":a,
"Windows 95":e,
"Win95":e,
"Linux":x,
"FreeBSD":m,
"NetBSD":y,
"SunOS":n,
"Symbian System":C,
"Nitro":q,
"PSP":"sonypsp",
"Mac OS X 10_5":h,
"Mac OS X 10.5":h,
"Mac OS X 10_4":d,
"Mac OS X 10.4":d,
"Mac OS X 10_3":i,
"Mac OS X 10.3":i,
"Mac OS X 10_2":b,
"Mac OS X 10.2":b,
"Mac OS X 10_1":g,
"Mac OS X 10.1":g,
"Mac OS X 10_0":c,
"Mac OS X 10.0":c,
"Mac OS X":w,
"Mac OS 9":u},
__lJ:function(){var G=navigator.userAgent;
var H=[];
for(var I in this.__lI){H.push(I);
}var J=new RegExp(A+H.join(p).replace(/\./g,
v)+k,
D);
if(!J.test(G)){throw new Error("Could not detect system: "+G);
}
if(qx.bom.client.Engine.WEBKIT&&RegExp(F).test(navigator.userAgent)){this.IPHONE=true;
this.NAME="iphone";
}else{this.NAME=this.__lI[RegExp.$1];
this[this.NAME.toUpperCase()]=true;
if(qx.bom.client.Platform.WIN){if(G.indexOf(j)!==-1){this.SP1=true;
}else if(qx.bom.client.Engine.MSHTML&&G.indexOf(o)!==-1){this.SP2=true;
}}}}},
defer:function(K){K.__lJ();
}});
})();
(function(){var a="Lucida Grande",
b="Liberation Sans",
c="Arial",
d="Tahoma",
e="Candara",
f="Segoe UI",
g="Consolas",
h="monospace",
i="Courier New",
j="qx.theme.modern.Font",
k="DejaVu Sans Mono";
qx.Theme.define(j,
{fonts:{"default":{size:qx.bom.client.System.WINVISTA?12:11,
lineHeight:1.4,
family:qx.bom.client.Platform.MAC?[a]:qx.bom.client.System.WINVISTA?[f,
e]:[d,
b,
c]},
"bold":{size:qx.bom.client.System.WINVISTA?12:11,
lineHeight:1.4,
family:qx.bom.client.Platform.MAC?[a]:qx.bom.client.System.WINVISTA?[f,
e]:[d,
b,
c],
bold:true},
"small":{size:qx.bom.client.System.WINVISTA?11:10,
lineHeight:1.4,
family:qx.bom.client.Platform.MAC?[a]:qx.bom.client.System.WINVISTA?[f,
e]:[d,
b,
c]},
"monospace":{size:11,
lineHeight:1.4,
family:qx.bom.client.Platform.MAC?[a]:qx.bom.client.System.WINVISTA?[g]:[g,
k,
i,
h]}}});
})();
(function(){var a="undefined",
b="button-frame",
c="widget",
d="atom",
e="main",
f="text-label",
g="middle",
h="background-light",
i="groupbox",
j="bold",
k="menu-button",
l="decoration/arrows/down.png",
m="text-selected",
n="button",
o="spinner",
p="input",
q="selected",
r="popup",
s="image",
t="text-disabled",
u="tree-item",
v="treevirtual-contract",
w="scrollbar",
x="datechooser/nav-button",
y="text-hovered",
z="center",
A="toolbar-button",
B="treevirtual-expand",
C="textfield",
D="tooltip",
E="label",
F="input-focused",
G="decoration/arrows/right.png",
H="background-application",
I="radiobutton",
J="list",
K="combobox",
L="checkbox",
M="text-title",
N="scrollbar/button",
O="combobox/button",
P="decoration/tree/closed.png",
Q="scrollbar-slider-horizontal",
R="decoration/arrows/left.png",
S="button-focused",
T="text-light",
U="text-input",
V="icon/16/places/folder.png",
W="slidebar/button-forward",
X="right-top",
Y="background-splitpane",
ba=".png",
bb="decoration/tree/open.png",
bc="datechooser",
bd="slidebar/button-backward",
be="selectbox",
bf="treevirtual-folder",
bg="shadow-popup",
bh="background-medium",
bi="table",
bj="decoration/form/",
bk="icon/16/places/folder-open.png",
bl="icon/16/mimetypes/office-document.png",
bm="qx/static/blank.gif",
bn="button-checked",
bo="decoration/window/maximize-active-hovered.png",
bp="radiobutton-hovered",
bq="decoration/cursors/",
br="slidebar",
bs="menu",
bt="table-scroller-focus-indicator",
bu="move-frame",
bv="nodrop",
bw="table-header-cell",
bx="app-header",
by="text-inactive",
bz="move",
bA="radiobutton-checked-focused",
bB="decoration/window/restore-active-hovered.png",
bC="shadow-window",
bD="table-column-button",
bE="right.png",
bF="tabview-page-button-bottom-inactive",
bG="window-statusbar",
bH="button-hovered",
bI="decoration/scrollbar/scrollbar-",
bJ="background-tip",
bK="table-scroller-header",
bL="radiobutton-disabled",
bM="button-pressed",
bN="table-pane",
bO="white",
bP="decoration/window/close-active.png",
bQ="tabview-page-button-left-active",
bR="checkbox-hovered",
bS="checkbox-checked",
bT="decoration/window/minimize-active-hovered.png",
bU="window-captionbar-active",
bV="menubar",
bW="tabview-page-button-top-inactive",
bX="tabview-page-button-left-inactive",
bY="toolbar-button-checked",
ca="decoration/tree/open-selected.png",
cb="radiobutton-checked",
cc="decoration/window/minimize-inactive.png",
cd="icon/16/apps/office-calendar.png",
ce="group",
cf="tabview-page-button-right-inactive",
cg="decoration/window/minimize-active.png",
ch="decoration/window/restore-inactive.png",
ci="text-active",
cj="checkbox-checked-focused",
ck="splitpane",
cl="toolbar-separator",
cm="button-preselected-focused",
cn="decoration/window/close-active-hovered.png",
co="toolbar",
cp="checkbox-pressed",
cq="border-separator",
cr="decoration/window/maximize-inactive.png",
cs="icon/22/places/folder-open.png",
ct="scrollarea",
cu="scrollbar-vertical",
cv="icon/22/mimetypes/office-document.png",
cw="button-preselected",
cx="static/blank.gif",
cy="button-checked-focused",
cz="up.png",
cA="decoration/tree/closed-selected.png",
cB="qx.theme.modern.Appearance",
cC="default",
cD="checkbox-disabled",
cE="toolbar-button-hovered",
cF="progressive-table-header",
cG="decoration/menu/radiobutton.gif",
cH="decoration/arrows/down-small.png",
cI="decoration/arrows/forward.png",
cJ="decoration/table/descending.png",
cK="checkbox-checked-hovered",
cL="scrollbar-slider-vertical",
cM="alias",
cN="decoration/window/restore-active.png",
cO="checkbox-checked-disabled",
cP="icon/32/mimetypes/office-document.png",
cQ="radiobutton-checked-disabled",
cR="tabview-pane",
cS="decoration/arrows/rewind.png",
cT="checkbox-focused",
cU="top",
cV="right",
cW="radiobutton-checked-hovered",
cX="table-header-cell-hovered",
cY="window",
da="text-gray",
db="decoration/menu/radiobutton-invert.gif",
dc="slider",
dd="decoration/table/select-column-order.png",
de="decoration/toolbar/toolbar-handle-knob.png",
df="down.png",
dg="tabview-page-button-top-active",
dh="icon/32/places/folder-open.png",
di="icon/22/places/folder.png",
dj="decoration/window/maximize-active.png",
dk="checkbox-checked-pressed",
dl="decoration/window/close-inactive.png",
dm="toolbar-part",
dn="decoration/splitpane/knob-vertical.png",
dp="left.png",
dq="decoration/menu/checkbox-invert.gif",
dr="decoration/arrows/up.png",
ds="radiobutton-checked-pressed",
dt="table-statusbar",
du="radiobutton-pressed",
dv="window-captionbar-inactive",
dw="copy",
dx="radiobutton-focused",
dy="decoration/menu/checkbox.gif",
dz="decoration/splitpane/knob-horizontal.png",
dA="icon/32/places/folder.png",
dB="tabview-page-button-bottom-active",
dC="decoration/arrows/up-small.png",
dD="decoration/table/ascending.png",
dE="small",
dF="tabview-page-button-right-active",
dG="scrollbar-horizontal",
dH="progressive-table-header-cell",
dI="menu-separator",
dJ="pane",
dK="decoration/arrows/right-invert.png",
dL=".gif",
dM="icon/16/actions/view-refresh.png";
qx.Theme.define(cB,
{appearances:{"widget":{},
"root":{style:function(dN){return {backgroundColor:H,
textColor:f,
font:cC};
}},
"label":{style:function(dN){return {textColor:dN.disabled?t:a};
}},
"move-frame":{style:function(dN){return {decorator:e};
}},
"resize-frame":bu,
"dragdrop-cursor":{style:function(dN){var dO=bv;
if(dN.copy){dO=dw;
}else if(dN.move){dO=bz;
}else if(dN.alias){dO=cM;
}return {source:bq+dO+dL,
position:X,
offset:[2,
16,
2,
6]};
}},
"image":{style:function(dN){return {opacity:!dN.replacement&&dN.disabled?0.3:1};
}},
"atom":{},
"atom/label":E,
"atom/icon":s,
"popup":{style:function(dN){return {decorator:e,
backgroundColor:h,
shadow:bg};
}},
"button-frame":{alias:d,
style:function(dN){var dP,
dQ;
if(dN.checked&&dN.focused&&!dN.inner){dP=cy;
dQ=f;
}else if(dN.checked){dP=bn;
dQ=f;
}else if(dN.pressed){dP=bM;
dQ=y;
}else if(dN.hovered){dP=bH;
dQ=y;
}else if(dN.preselected&&dN.focused&&!dN.inner){dP=cm;
dQ=y;
}else if(dN.preselected){dP=cw;
dQ=y;
}else if(dN.focused&&!dN.inner){dP=S;
dQ=f;
}else{dP=n;
dQ=f;
}return {decorator:dP,
textColor:dQ};
}},
"button":{alias:b,
include:b,
style:function(dN){return {padding:[2,
8],
center:true};
}},
"splitbutton":{},
"splitbutton/button":n,
"splitbutton/arrow":{alias:n,
include:n,
style:function(dN){return {icon:l,
padding:2,
marginLeft:1};
}},
"checkbox":{alias:d,
style:function(dN){var dO;
if(dN.checked&&dN.focused){dO=cj;
}else if(dN.checked&&dN.disabled){dO=cO;
}else if(dN.checked&&dN.pressed){dO=dk;
}else if(dN.checked&&dN.hovered){dO=cK;
}else if(dN.checked){dO=bS;
}else if(dN.disabled){dO=cD;
}else if(dN.focused){dO=cT;
}else if(dN.pressed){dO=cp;
}else if(dN.hovered){dO=bR;
}else{dO=L;
}return {icon:bj+dO+ba,
gap:6};
}},
"radiobutton":{alias:d,
style:function(dN){var dO;
if(dN.checked&&dN.focused){dO=bA;
}else if(dN.checked&&dN.disabled){dO=cQ;
}else if(dN.checked&&dN.pressed){dO=ds;
}else if(dN.checked&&dN.hovered){dO=cW;
}else if(dN.checked){dO=cb;
}else if(dN.disabled){dO=bL;
}else if(dN.focused){dO=dx;
}else if(dN.pressed){dO=du;
}else if(dN.hovered){dO=bp;
}else{dO=I;
}return {icon:bj+dO+ba,
gap:6};
}},
"textfield":{style:function(dN){return {decorator:dN.focused?F:p,
padding:[2,
4,
1],
textColor:dN.disabled?t:U};
}},
"textarea":{style:function(dN){return {decorator:dN.focused?F:p,
padding:4,
textColor:dN.disabled?t:U};
}},
"spinner":{style:function(dN){return {decorator:dN.focused?F:p};
}},
"spinner/textfield":{include:C,
style:function(dN){return {decorator:a,
padding:[2,
4,
1]};
}},
"spinner/upbutton":{alias:b,
include:b,
style:function(dN){return {icon:dC,
padding:dN.pressed?[2,
2,
0,
4]:[1,
3,
1,
3]};
}},
"spinner/downbutton":{alias:b,
include:b,
style:function(dN){return {icon:cH,
padding:dN.pressed?[2,
2,
0,
4]:[1,
3,
1,
3]};
}},
"datefield":K,
"datefield/button":{alias:O,
include:O,
style:function(dN){return {icon:cd,
padding:[0,
3],
decorator:a};
}},
"datefield/textfield":{style:function(dN){return {padding:[2,
4,
1]};
}},
"datefield/list":{alias:bc,
include:bc,
style:function(dN){return {decorator:a};
}},
"groupbox":{style:function(dN){return {legendPosition:cU};
}},
"groupbox/legend":{alias:d,
style:function(dN){return {padding:[1,
0,
1,
4],
textColor:M,
font:j};
}},
"groupbox/frame":{style:function(dN){return {padding:12,
decorator:ce};
}},
"check-groupbox":i,
"check-groupbox/legend":{alias:L,
include:L,
style:function(dN){return {padding:[1,
0,
1,
4],
textColor:M,
font:j};
}},
"radio-groupbox":i,
"radio-groupbox/legend":{alias:I,
include:I,
style:function(dN){return {padding:[1,
0,
1,
4],
textColor:M};
}},
"scrollarea":c,
"scrollarea/corner":{style:function(dN){return {backgroundColor:H};
}},
"scrollarea/pane":c,
"scrollarea/scrollbar-x":w,
"scrollarea/scrollbar-y":w,
"scrollbar":{style:function(dN){return {width:dN.horizontal?a:16,
height:dN.horizontal?16:a,
decorator:dN.horizontal?dG:cu,
padding:1};
}},
"scrollbar/slider":{alias:dc,
style:function(dN){return {padding:dN.horizontal?[0,
1,
0,
1]:[1,
0,
1,
0]};
}},
"scrollbar/slider/knob":{include:b,
style:function(dN){return {decorator:dN.horizontal?Q:cL,
minHeight:dN.horizontal?a:14,
minWidth:dN.horizontal?14:a};
}},
"scrollbar/button":{alias:b,
include:b,
style:function(dN){var dO=bI;
if(dN.left){dO+=dp;
}else if(dN.right){dO+=bE;
}else if(dN.up){dO+=cz;
}else{dO+=df;
}
if(dN.left||dN.right){return {padding:[0,
0,
0,
dN.left?3:4],
icon:dO,
width:15,
height:14};
}else{return {padding:[0,
0,
0,
2],
icon:dO,
width:14,
height:15};
}}},
"scrollbar/button-begin":N,
"scrollbar/button-end":N,
"slider":{style:function(dN){return {decorator:p};
}},
"slider/knob":{include:b,
style:function(dN){return {decorator:Q,
height:14,
width:14};
}},
"list":{alias:ct,
style:function(dN){return {backgroundColor:h,
decorator:e};
}},
"list/pane":c,
"listitem":{alias:d,
style:function(dN){return {padding:4,
textColor:dN.selected?m:a,
decorator:dN.selected?q:a};
}},
"slidebar":{},
"slidebar/scrollpane":{},
"slidebar/content":{},
"slidebar/button-forward":{alias:b,
include:b,
style:function(dN){return {padding:5,
center:true,
icon:dN.barLeft||dN.barRight?l:G};
}},
"slidebar/button-backward":{alias:b,
include:b,
style:function(dN){return {padding:5,
center:true,
icon:dN.barLeft||dN.barRight?dr:R};
}},
"tabview":{style:function(dN){return {contentPadding:16};
}},
"tabview/bar":{alias:br,
style:function(dN){var dR={marginBottom:dN.barTop?-1:0,
marginTop:dN.barBottom?-4:0,
marginLeft:dN.barRight?-3:0,
marginRight:dN.barLeft?-1:0,
paddingTop:0,
paddingRight:0,
paddingBottom:0,
paddingLeft:0};
if(dN.barTop||dN.barBottom){dR.paddingLeft=5;
dR.paddingRight=7;
}else{dR.paddingTop=5;
dR.paddingBottom=7;
}return dR;
}},
"tabview/bar/button-forward":{include:W,
alias:W,
style:function(dN){if(dN.barTop||dN.barBottom){return {marginTop:2,
marginBottom:2};
}else{return {marginLeft:2,
marginRight:2};
}}},
"tabview/bar/button-backward":{include:bd,
alias:bd,
style:function(dN){if(dN.barTop||dN.barBottom){return {marginTop:2,
marginBottom:2};
}else{return {marginLeft:2,
marginRight:2};
}}},
"tabview/bar/scrollpane":{},
"tabview/pane":{style:function(dN){return {decorator:cR,
minHeight:100,
marginBottom:dN.barBottom?-1:0,
marginTop:dN.barTop?-1:0,
marginLeft:dN.barLeft?-1:0,
marginRight:dN.barRight?-1:0};
}},
"tabview-page":c,
"tabview-page/button":{alias:d,
style:function(dN){var dP,
dS=0;
var dT=0,
dU=0,
dV=0,
dW=0;
if(dN.checked){if(dN.barTop){dP=dg;
dS=[6,
14];
dV=dN.firstTab?0:-5;
dW=dN.lastTab?0:-5;
}else if(dN.barBottom){dP=dB;
dS=[6,
14];
dV=dN.firstTab?0:-5;
dW=dN.lastTab?0:-5;
}else if(dN.barRight){dP=dF;
dS=[6,
13];
dT=dN.firstTab?0:-5;
dU=dN.lastTab?0:-5;
}else{dP=bQ;
dS=[6,
13];
dT=dN.firstTab?0:-5;
dU=dN.lastTab?0:-5;
}}else{if(dN.barTop){dP=bW;
dS=[4,
10];
dT=4;
dV=dN.firstTab?5:1;
dW=1;
}else if(dN.barBottom){dP=bF;
dS=[4,
10];
dU=4;
dV=dN.firstTab?5:1;
dW=1;
}else if(dN.barRight){dP=cf;
dS=[4,
10];
dW=5;
dT=dN.firstTab?5:1;
dU=1;
dV=1;
}else{dP=bX;
dS=[4,
10];
dV=5;
dT=dN.firstTab?5:1;
dU=1;
dW=1;
}}return {zIndex:dN.checked?10:5,
decorator:dP,
padding:dS,
marginTop:dT,
marginBottom:dU,
marginLeft:dV,
marginRight:dW,
textColor:dN.checked?ci:by};
}},
"toolbar":{style:function(dN){return {decorator:co,
spacing:2};
}},
"toolbar/part":{style:function(dN){return {decorator:dm,
spacing:2};
}},
"toolbar/part/container":{style:function(dN){return {paddingLeft:2,
paddingRight:2};
}},
"toolbar/part/handle":{style:function(dN){return {source:de,
marginLeft:3,
marginRight:3};
}},
"toolbar-button":{alias:d,
style:function(dN){return {marginTop:2,
marginBottom:2,
padding:dN.pressed||dN.checked||dN.hovered?3:5,
decorator:dN.pressed||dN.checked?bY:dN.hovered?cE:a,
textColor:dN.disabled?t:a};
}},
"toolbar-splitbutton":{style:function(dN){return {marginTop:2,
marginBottom:2};
}},
"toolbar-splitbutton/button":{alias:A,
include:A,
style:function(dN){return {icon:l,
marginTop:a,
marginBottom:a};
}},
"toolbar-splitbutton/arrow":{alias:A,
include:A,
style:function(dN){return {padding:dN.pressed||dN.checked?1:dN.hovered?1:3,
icon:l,
marginTop:a,
marginBottom:a};
}},
"toolbar-separator":{style:function(dN){return {decorator:cl,
margin:7};
}},
"tree":J,
"tree-item":{style:function(dN){return {padding:[2,
6],
textColor:dN.selected?m:a,
decorator:dN.selected?q:a};
}},
"tree-item/icon":{include:s,
style:function(dN){return {paddingRight:5};
}},
"tree-item/label":E,
"tree-item/open":{include:s,
style:function(dN){var dO;
if(dN.selected&&dN.opened){dO=ca;
}else if(dN.selected&&!dN.opened){dO=cA;
}else if(dN.opened){dO=bb;
}else{dO=P;
}return {padding:[0,
5,
0,
2],
source:dO};
}},
"tree-folder":{include:u,
alias:u,
style:function(dN){var dO;
if(dN.small){dO=dN.opened?bk:V;
}else if(dN.large){dO=dN.opened?dh:dA;
}else{dO=dN.opened?cs:di;
}return {icon:dO};
}},
"tree-file":{include:u,
alias:u,
style:function(dN){return {icon:dN.small?bl:dN.large?cP:cv};
}},
"treevirtual":bi,
"treevirtual-folder":{style:function(dN){return {icon:(dN.opened?bk:V)};
}},
"treevirtual-file":{include:bf,
alias:bf,
style:function(dN){return {icon:bl};
}},
"treevirtual-line":{style:function(dN){return {icon:cx};
}},
"treevirtual-contract":{style:function(dN){return {icon:bb,
paddingLeft:3};
}},
"treevirtual-expand":{style:function(dN){return {icon:P,
paddingLeft:5};
}},
"treevirtual-only-contract":v,
"treevirtual-only-expand":B,
"treevirtual-start-contract":v,
"treevirtual-start-expand":B,
"treevirtual-end-contract":v,
"treevirtual-end-expand":B,
"treevirtual-cross-contract":v,
"treevirtual-cross-expand":B,
"treevirtual-end":{style:function(dN){return {icon:bm};
}},
"treevirtual-cross":{style:function(dN){return {icon:bm};
}},
"tooltip":{include:r,
style:function(dN){return {backgroundColor:bJ,
padding:[1,
3,
2,
3],
offset:[1,
1,
20,
1]};
}},
"tooltip/atom":d,
"window":{style:function(dN){return {shadow:bC,
contentPadding:[10,
10,
10,
10]};
}},
"window/pane":{style:function(dN){return {decorator:cY};
}},
"window/captionbar":{style:function(dN){return {decorator:dN.active?bU:dv,
textColor:dN.active?bO:da,
minHeight:26,
paddingRight:2};
}},
"window/icon":{style:function(dN){return {margin:[5,
0,
3,
6]};
}},
"window/title":{style:function(dN){return {alignY:g,
font:j,
marginLeft:6,
marginRight:12};
}},
"window/minimize-button":{alias:d,
style:function(dN){return {icon:dN.active?dN.hovered?bT:cg:cc,
margin:[4,
8,
2,
0]};
}},
"window/restore-button":{alias:d,
style:function(dN){return {icon:dN.active?dN.hovered?bB:cN:ch,
margin:[5,
8,
2,
0]};
}},
"window/maximize-button":{alias:d,
style:function(dN){return {icon:dN.active?dN.hovered?bo:dj:cr,
margin:[4,
8,
2,
0]};
}},
"window/close-button":{alias:d,
style:function(dN){return {icon:dN.active?dN.hovered?cn:bP:dl,
margin:[4,
8,
2,
0]};
}},
"window/statusbar":{style:function(dN){return {padding:[2,
6],
decorator:bG,
minHeight:18};
}},
"window/statusbar-text":{style:function(dN){return {font:dE,
textColor:f};
}},
"iframe":{style:function(dN){return {decorator:e};
}},
"resizer":{style:function(dN){return {decorator:dJ};
}},
"splitpane":{style:function(dN){return {decorator:ck};
}},
"splitpane/splitter":{style:function(dN){return {width:dN.horizontal?3:a,
height:dN.vertical?3:a,
backgroundColor:Y};
}},
"splitpane/splitter/knob":{style:function(dN){return {source:dN.horizontal?dz:dn};
}},
"splitpane/slider":{style:function(dN){return {width:dN.horizontal?3:a,
height:dN.vertical?3:a,
backgroundColor:Y};
}},
"selectbox":{alias:b,
include:b,
style:function(dN){return {padding:[2,
8]};
}},
"selectbox/atom":d,
"selectbox/popup":r,
"selectbox/list":{alias:J},
"selectbox/arrow":{style:function(dN){return {source:l,
paddingRight:4,
paddingLeft:5};
}},
"datechooser":{style:function(dN){return {padding:2,
decorator:e,
backgroundColor:h};
}},
"datechooser/navigation-bar":{},
"datechooser/nav-button":{include:b,
alias:b,
style:function(dN){var dR={padding:[2,
4]};
if(dN.lastYear){dR.icon=cS;
dR.marginRight=1;
}else if(dN.lastMonth){dR.icon=R;
}else if(dN.nextYear){dR.icon=cI;
dR.marginLeft=1;
}else if(dN.nextMonth){dR.icon=G;
}return dR;
}},
"datechooser/last-year-button-tooltip":D,
"datechooser/last-month-button-tooltip":D,
"datechooser/next-year-button-tooltip":D,
"datechooser/next-month-button-tooltip":D,
"datechooser/last-year-button":x,
"datechooser/last-month-button":x,
"datechooser/next-month-button":x,
"datechooser/next-year-button":x,
"datechooser/month-year-label":{style:function(dN){return {font:j,
textAlign:z};
}},
"datechooser/date-pane":{style:function(dN){return {marginTop:2};
}},
"datechooser-weekday":{style:function(dN){return {textColor:dN.weekend?T:a,
textAlign:z,
paddingTop:2,
backgroundColor:bh};
}},
"datechooser-week":{style:function(dN){return {textAlign:z,
textColor:f,
padding:[2,
4],
backgroundColor:bh};
}},
"datechooser-day":{style:function(dN){return {textAlign:z,
decorator:dN.selected?q:a,
textColor:dN.selected?m:dN.otherMonth?T:a,
font:dN.today?j:a,
padding:[2,
4]};
}},
"combobox":{style:function(dN){return {decorator:dN.focused?F:p};
}},
"combobox/popup":r,
"combobox/list":{alias:J},
"combobox/button":{include:b,
alias:b,
style:function(dN){var dX={icon:l,
padding:2};
if(dN.selected){dX.decorator=S;
}return dX;
}},
"combobox/textfield":{include:C,
style:function(dN){return {decorator:null,
padding:[2,
4,
1]};
}},
"menu":{style:function(dN){var dR={decorator:bs,
shadow:bg,
spacingX:6,
spacingY:1,
iconColumnWidth:16,
arrowColumnWidth:4};
if(dN.submenu){dR.position=X;
dR.offset=[-2,
-3];
}return dR;
}},
"menu-separator":{style:function(dN){return {height:0,
decorator:dI,
margin:[4,
2]};
}},
"menu-button":{alias:d,
style:function(dN){return {decorator:dN.selected?q:a,
textColor:dN.selected?m:a,
padding:[4,
6]};
}},
"menu-button/icon":{include:s,
style:function(dN){return {alignY:g};
}},
"menu-button/label":{include:E,
style:function(dN){return {alignY:g,
padding:1};
}},
"menu-button/shortcut":{include:E,
style:function(dN){return {alignY:g,
marginLeft:14,
padding:1};
}},
"menu-button/arrow":{style:function(dN){return {source:dN.selected?dK:G,
alignY:g};
}},
"menu-checkbox":{alias:k,
include:k,
style:function(dN){return {icon:!dN.checked?a:dN.selected?dq:dy};
}},
"menu-radiobutton":{alias:k,
include:k,
style:function(dN){return {icon:!dN.checked?a:dN.selected?db:cG};
}},
"menubar":{style:function(dN){return {decorator:bV};
}},
"menubar-button":{alias:d,
style:function(dN){return {decorator:dN.pressed||dN.hovered?q:a,
textColor:dN.pressed||dN.hovered?m:f,
padding:[3,
8]};
}},
"colorselector":c,
"colorselector/control-bar":c,
"colorselector/control-pane":c,
"colorselector/visual-pane":i,
"colorselector/preset-grid":c,
"colorselector-colorbucket":{style:function(dN){return {decorator:e,
width:16,
height:16};
}},
"colorselector/preset-field-set":i,
"colorselector/input-field-set":i,
"colorselector/preview-field-set":i,
"colorselector/hex-field-composite":c,
"colorselector/hex-field":C,
"colorselector/rgb-spinner-composite":c,
"colorselector/rgb-spinner-red":o,
"colorselector/rgb-spinner-green":o,
"colorselector/rgb-spinner-blue":o,
"colorselector/hsb-spinner-composite":c,
"colorselector/hsb-spinner-hue":o,
"colorselector/hsb-spinner-saturation":o,
"colorselector/hsb-spinner-brightness":o,
"colorselector/preview-content-old":{style:function(dN){return {decorator:e,
width:50,
height:10};
}},
"colorselector/preview-content-new":{style:function(dN){return {decorator:e,
backgroundColor:h,
width:50,
height:10};
}},
"colorselector/hue-saturation-field":{style:function(dN){return {decorator:e,
margin:5};
}},
"colorselector/brightness-field":{style:function(dN){return {decorator:e,
margin:[5,
7]};
}},
"colorselector/hue-saturation-pane":c,
"colorselector/hue-saturation-handle":c,
"colorselector/brightness-pane":c,
"colorselector/brightness-handle":c,
"colorpopup":{alias:r,
include:r,
style:function(dN){return {padding:5,
backgroundColor:H};
}},
"colorpopup/field":{style:function(dN){return {decorator:e,
margin:2,
width:14,
height:14,
backgroundColor:h};
}},
"colorpopup/selector-button":n,
"colorpopup/auto-button":n,
"colorpopup/preview-pane":i,
"colorpopup/current-preview":{style:function(dY){return {height:20,
padding:4,
marginLeft:4,
decorator:e,
allowGrowX:true};
}},
"colorpopup/selected-preview":{style:function(dY){return {height:20,
padding:4,
marginRight:4,
decorator:e,
allowGrowX:true};
}},
"table":{alias:c,
style:function(dN){return {decorator:bi};
}},
"table-header":{},
"table/statusbar":{style:function(dN){return {decorator:dt,
padding:[0,
2]};
}},
"table/column-button":{alias:b,
style:function(dN){return {decorator:bD,
padding:3,
icon:dd};
}},
"table-column-reset-button":{include:k,
alias:k,
style:function(){return {icon:dM};
}},
"table-scroller":c,
"table-scroller/scrollbar-x":w,
"table-scroller/scrollbar-y":w,
"table-scroller/header":{style:function(dN){return {decorator:bK};
}},
"table-scroller/pane":{style:function(dN){return {backgroundColor:bN};
}},
"table-scroller/focus-indicator":{style:function(dN){return {decorator:bt};
}},
"table-scroller/resize-line":{style:function(dN){return {backgroundColor:cq,
width:2};
}},
"table-header-cell":{alias:d,
style:function(dN){return {minWidth:40,
minHeight:20,
padding:dN.hovered?[3,
4,
2,
4]:[3,
4],
decorator:dN.hovered?cX:bw,
sortIcon:dN.sorted?(dN.sortedAscending?dD:cJ):a};
}},
"table-header-cell/label":{style:function(dN){return {minWidth:0,
alignY:g,
paddingRight:5};
}},
"table-header-cell/sort-icon":{style:function(dN){return {alignY:g,
alignX:cV};
}},
"table-header-cell/icon":{style:function(dN){return {minWidth:0,
alignY:g,
paddingRight:5};
}},
"table-editor-textfield":{include:C,
style:function(dN){return {decorator:a,
padding:[2,
2],
backgroundColor:h};
}},
"table-editor-selectbox":{include:be,
alias:be,
style:function(dN){return {padding:[0,
2],
backgroundColor:h};
}},
"table-editor-combobox":{include:K,
alias:K,
style:function(dN){return {decorator:a,
backgroundColor:h};
}},
"progressive-table-header":{alias:c,
style:function(dN){return {decorator:cF};
}},
"progressive-table-header-cell":{alias:d,
style:function(dN){return {minWidth:40,
minHeight:25,
paddingLeft:6,
decorator:dH};
}},
"app-header":{style:function(dN){return {font:j,
textColor:m,
padding:[8,
12],
decorator:bx};
}}}});
})();
(function(){var a="Tango",
b="qx/icon/Tango",
c="qx.theme.icon.Tango";
qx.Theme.define(c,
{title:a,
resource:b,
icons:{}});
})();
(function(){var a="qx.theme.Modern",
b="Modern";
qx.Theme.define(a,
{title:b,
meta:{color:qx.theme.modern.Color,
decoration:qx.theme.modern.Decoration,
font:qx.theme.modern.Font,
appearance:qx.theme.modern.Appearance,
icon:qx.theme.icon.Tango}});
})();
(function(){var a="white",
b="black",
c="#3E6CA8",
d="#EBE9ED",
e="#A7A6AA",
f="#EEE",
g="#F3F0F5",
h="gray",
i="#888888",
j="#3E5B97",
k="#FFFFE1",
l="#F3F8FD",
m="#808080",
n="#F4F4F4",
o="#CCCCCC",
p="#DBEAF9",
q="#BCCEE5",
r="#A5BDDE",
s="#7CA0CF",
t="#F6F5F7",
u="qx.theme.classic.Color",
v="#F9F8E9",
w="#DCDFE4",
x="#FAFBFE",
y="#AAAAAA",
z="#85878C";
qx.Theme.define(u,
{colors:{"background":d,
"background-light":g,
"background-focused":l,
"background-focused-inner":p,
"background-disabled":n,
"background-selected":c,
"background-field":a,
"background-pane":x,
"border-lead":i,
"border-light":a,
"border-light-shadow":w,
"border-dark-shadow":e,
"border-dark":z,
"border-focused-light":q,
"border-focused-light-shadow":r,
"border-focused-dark-shadow":s,
"border-focused-dark":c,
"border-separator":m,
"text":b,
"text-disabled":e,
"text-selected":a,
"text-focused":j,
"tooltip":k,
"tooltip-text":b,
"button":d,
"button-hovered":t,
"button-abandoned":v,
"button-checked":g,
"window-active-caption-text":[255,
255,
255],
"window-inactive-caption-text":[255,
255,
255],
"window-active-caption":[51,
94,
168],
"window-inactive-caption":[111,
161,
217],
"date-chooser":a,
"date-chooser-title":[116,
116,
116],
"date-chooser-selected":[52,
52,
52],
"effect":[254,
200,
60],
"table-pane":a,
"table-header":[242,
242,
242],
"table-header-border":[214,
213,
217],
"table-header-cell":[235,
234,
219],
"table-header-cell-hover":[255,
255,
255],
"table-focus-indicator":[179,
217,
255],
"table-row-background-focused-selected":[90,
138,
211],
"table-row-background-focused":[221,
238,
255],
"table-row-background-selected":[51,
94,
168],
"table-row-background-even":[250,
248,
243],
"table-row-background-odd":[255,
255,
255],
"table-row-selected":[255,
255,
255],
"table-row":[0,
0,
0],
"table-row-line":f,
"table-column-line":f,
"progressive-table-header":y,
"progressive-table-row-background-even":[250,
248,
243],
"progressive-table-row-background-odd":[255,
255,
255],
"progressive-progressbar-background":h,
"progressive-progressbar-indicator-done":o,
"progressive-progressbar-indicator-undone":a,
"progressive-progressbar-percent-background":h,
"progressive-progressbar-percent-text":a}});
})();
(function(){var a="px ",
b=" ",
c=";",
d="Color",
e="Number",
f="border-left:",
g="px",
h="border-bottom:",
i="border-top:",
j="shorthand",
k="border-right:",
l="innerWidthRight",
m='<div style="position:absolute;top:0;left:0;',
n="innerColorBottom",
o='</div>',
p='',
q="scale",
r="innerColorRight",
s="innerColorTop",
t="innerColorLeft",
u="__lK",
v="qx.ui.decoration.Double",
w="",
x='">',
y="innerWidthBottom",
z="innerWidthLeft",
A="__lL",
B="innerWidthTop";
qx.Class.define(v,
{extend:qx.ui.decoration.Single,
construct:function(C,
D,
E,
F,
G){arguments.callee.base.call(this,
C,
D,
E,
F,
G);
if(F!=null){this.setInnerWidth(F);
}
if(G!=null){this.setInnerColor(G);
}},
properties:{innerWidthTop:{check:e,
init:0},
innerWidthRight:{check:e,
init:0},
innerWidthBottom:{check:e,
init:0},
innerWidthLeft:{check:e,
init:0},
innerWidth:{group:[B,
l,
y,
z],
mode:j},
innerColorTop:{nullable:true,
check:d},
innerColorRight:{nullable:true,
check:d},
innerColorBottom:{nullable:true,
check:d},
innerColorLeft:{nullable:true,
check:d},
innerColor:{group:[s,
r,
n,
t],
mode:j}},
members:{__lK:null,
__lL:null,
getMarkup:function(){if(this.__lK){return this.__lK;
}var H=qx.theme.manager.Color.getInstance();
var I=w;
var C=this.getInnerWidthTop();
if(C>0){I+=i+C+a+this.getStyleTop()+b+H.resolve(this.getInnerColorTop())+c;
}var C=this.getInnerWidthRight();
if(C>0){I+=k+C+a+this.getStyleRight()+b+H.resolve(this.getInnerColorRight())+c;
}var C=this.getInnerWidthBottom();
if(C>0){I+=h+C+a+this.getStyleBottom()+b+H.resolve(this.getInnerColorBottom())+c;
}var C=this.getInnerWidthLeft();
if(C>0){I+=f+C+a+this.getStyleLeft()+b+H.resolve(this.getInnerColorLeft())+c;
}{};
var J=qx.ui.decoration.Util.generateBackgroundMarkup(this.getBackgroundImage(),
this.getBackgroundRepeat(),
I);
var K=p;
var C=this.getWidthTop();
if(C>0){K+=i+C+a+this.getStyleTop()+b+H.resolve(this.getColorTop())+c;
}var C=this.getWidthRight();
if(C>0){K+=k+C+a+this.getStyleRight()+b+H.resolve(this.getColorRight())+c;
}var C=this.getWidthBottom();
if(C>0){K+=h+C+a+this.getStyleBottom()+b+H.resolve(this.getColorBottom())+c;
}var C=this.getWidthLeft();
if(C>0){K+=f+C+a+this.getStyleLeft()+b+H.resolve(this.getColorLeft())+c;
}{};
return this.__lK=m+K+x+J+o;
},
resize:function(L,
C,
M){var N=this.getBackgroundImage()&&this.getBackgroundRepeat()==q;
if(N||qx.bom.client.Feature.CONTENT_BOX){var O=this.getInsets();
var F=C-O.left-O.right;
var P=M-O.top-O.bottom;
}else{var F=C-this.getWidthLeft()-this.getWidthRight();
var P=M-this.getWidthTop()-this.getWidthBottom();
}if(F<0){F=0;
}
if(P<0){P=0;
}var Q=L.getDomElement();
Q.firstChild.style.width=F+g;
Q.firstChild.style.height=P+g;
},
getInsets:function(){if(this.__lL){return this.__lL;
}this.__lL={top:this.getWidthTop()+this.getInnerWidthTop(),
right:this.getWidthRight()+this.getInnerWidthRight(),
bottom:this.getWidthBottom()+this.getInnerWidthBottom(),
left:this.getWidthLeft()+this.getInnerWidthLeft()};
return this.__lL;
}},
destruct:function(){this._disposeFields(u,
A);
}});
})();
(function(){var a="border-dark-shadow",
b="border-light",
c="border-dark",
d="border-light-shadow",
e="solid",
f="gray",
g="border-focused-light",
h="border-focused-dark",
i="border-focused-light-shadow",
j="border-focused-dark-shadow",
k="table-header-border",
l="#ffffff",
m="border-separator",
n="#a7a6aa",
o="dotted",
p="effect",
q="tooltip-text",
r="table-focus-indicator",
s="qx/decoration/Classic",
t="border-lead",
u="decoration/shadow/shadow-small.png",
v="qx.theme.classic.Decoration",
w="decoration/shadow/shadow.png";
qx.Theme.define(v,
{resource:s,
decorations:{"main":{decorator:qx.ui.decoration.Uniform,
style:{width:1,
color:c}},
"inset":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[a,
b,
b,
a],
innerColor:[c,
d,
d,
c]}},
"outset":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[d,
c,
c,
d],
innerColor:[b,
a,
a,
b]}},
"groove":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[a,
b,
b,
a],
innerColor:[b,
a,
a,
b]}},
"ridge":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[b,
a,
a,
b],
innerColor:[a,
b,
b,
a]}},
"inset-thin":{decorator:qx.ui.decoration.Single,
style:{width:1,
color:[a,
b,
b,
a]}},
"outset-thin":{decorator:qx.ui.decoration.Single,
style:{width:1,
color:[b,
a,
a,
b]}},
"focused-inset":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[j,
g,
g,
j],
innerColor:[h,
i,
i,
h]}},
"focused-outset":{decorator:qx.ui.decoration.Double,
style:{width:1,
innerWidth:1,
color:[i,
h,
h,
i],
innerColor:[g,
j,
j,
g]}},
"separator-horizontal":{decorator:qx.ui.decoration.Single,
style:{widthLeft:1,
colorLeft:m}},
"separator-vertical":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:m}},
"shadow":{decorator:qx.ui.decoration.Grid,
style:{baseImage:w,
insets:[4,
8,
8,
4]}},
"shadow-small":{decorator:qx.ui.decoration.Grid,
style:{baseImage:u,
insets:[0,
3,
3,
0]}},
"lead-item":{decorator:qx.ui.decoration.Uniform,
style:{width:1,
style:o,
color:t}},
"tooltip":{decorator:qx.ui.decoration.Uniform,
style:{width:1,
color:q}},
"toolbar-separator":{decorator:qx.ui.decoration.Single,
style:{widthLeft:1,
colorLeft:a}},
"toolbar-part-handle":{decorator:qx.ui.decoration.Single,
style:{width:1,
style:e,
colorTop:l,
colorLeft:l,
colorRight:n,
colorBottom:n}},
"menu-separator":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
widthBottom:1,
colorTop:c,
colorBottom:b}},
"datechooser-date-pane":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:f,
style:e}},
"datechooser-weekday":{decorator:qx.ui.decoration.Single,
style:{widthBottom:1,
colorBottom:f,
style:e}},
"datechooser-week":{decorator:qx.ui.decoration.Single,
style:{widthRight:1,
colorRight:f,
style:e}},
"datechooser-week-header":{decorator:qx.ui.decoration.Single,
style:{widthBottom:1,
colorBottom:f,
widthRight:1,
colorRight:f,
style:e}},
"tabview-page-button-top":{decorator:qx.ui.decoration.Double,
style:{width:1,
color:[d,
c,
c,
d],
innerWidth:1,
innerColor:[b,
a,
a,
b],
widthBottom:0,
innerWidthBottom:0}},
"tabview-page-button-bottom":{decorator:qx.ui.decoration.Double,
style:{width:1,
color:[d,
c,
c,
d],
innerWidth:1,
innerColor:[b,
a,
a,
b],
widthTop:0,
innerWidthTop:0}},
"tabview-page-button-left":{decorator:qx.ui.decoration.Double,
style:{width:1,
color:[d,
c,
c,
d],
innerWidth:1,
innerColor:[b,
a,
a,
b],
widthRight:0,
innerWidthRight:0}},
"tabview-page-button-right":{decorator:qx.ui.decoration.Double,
style:{width:1,
color:[d,
c,
c,
d],
innerWidth:1,
innerColor:[b,
a,
a,
b],
widthLeft:0,
innerWidthLeft:0}},
"table-statusbar":{decorator:qx.ui.decoration.Single,
style:{widthTop:1,
colorTop:a,
styleTop:e}},
"table-scroller-header":{decorator:qx.ui.decoration.Single,
style:{widthBottom:1,
colorBottom:k,
styleBottom:e}},
"table-scroller-focus-indicator":{decorator:qx.ui.decoration.Single,
style:{width:2,
color:r,
style:e}},
"table-header-cell":{decorator:qx.ui.decoration.Single,
style:{widthRight:1,
colorRight:k,
styleRight:e}},
"table-header-cell-hovered":{decorator:qx.ui.decoration.Single,
style:{widthRight:1,
colorRight:k,
styleRight:e,
widthBottom:2,
colorBottom:p,
styleBottom:e}}}});
})();
(function(){var a="Liberation Sans",
b="Verdana",
c="Bitstream Vera Sans",
d="Lucida Grande",
e="Tahoma",
f="monospace",
g="qx.theme.classic.Font",
h="Courier New",
i="DejaVu Sans Mono";
qx.Theme.define(g,
{fonts:{"default":{size:11,
lineHeight:1.4,
family:[d,
e,
b,
c,
a]},
"bold":{size:11,
lineHeight:1.4,
family:[d,
e,
b,
c,
a],
bold:true},
"small":{size:10,
lineHeight:1.4,
family:[d,
e,
b,
c,
a]},
"monospace":{size:11,
lineHeight:1.4,
family:[i,
h,
f]}}});
})();
(function(){var a="button",
b="undefined",
c="background",
d="widget",
e="inset-thin",
f="atom",
g="outset",
h="inset",
i="groupbox",
j="focused-inset",
k="middle",
l="menu-button",
m="spinner",
n="tooltip",
o="text-disabled",
p="checkbox",
q="text-selected",
r="popup",
s="textfield",
t="list",
u="button-hovered",
v="shadow-small",
w="scrollbar",
x="center",
y="datechooser/button",
z="decoration/arrows/down.gif",
A="main",
B="background-selected",
C="date-chooser",
D="outset-thin",
E="label",
F="date-chooser-title",
G="radiobutton",
H="default",
I="bold",
J="white",
K="combobox",
L="background-focused",
M="toolbar-button",
N="button-abandoned",
O="background-light",
P="background-field",
Q="background-disabled",
R="scrollbar/button",
S="combobox/button",
T="table-header-cell",
U="image",
V="decoration/arrows/right.gif",
W="icon/16/places/folder.png",
X="tree-folder",
Y="icon/16/mimetypes/text-plain.png",
ba="right-top",
bb=".png",
bc="datechooser",
bd="slidebar/button-backward",
be="selectbox",
bf="slidebar/button-forward",
bg="treevirtual-folder",
bh="decoration/form/",
bi="decoration/tree/minus.gif",
bj="icon/16/places/folder-open.png",
bk="decoration/tree/plus.gif",
bl="decoration/arrows/left.gif",
bm="top",
bn="radiobutton-hovered",
bo="decoration/treevirtual/start_plus.gif",
bp="decoration/cursors/",
bq="slidebar",
br="table-scroller-focus-indicator",
bs="move-frame",
bt="nodrop",
bu="tabview-page-button-left",
bv="decoration/arrows/up-small.gif",
bw="move",
bx="radiobutton-checked-focused",
by="#D6D5D9",
bz="qx.theme.classic.Appearance",
bA="decoration/menu/checkbox.gif",
bB="decoration/arrows/rewind.gif",
bC="table-scroller-header",
bD="radiobutton-disabled",
bE="table-pane",
bF="focused-outset",
bG="checkbox-hovered",
bH="text",
bI="datechooser-date-pane",
bJ="background-pane",
bK="decoration/treevirtual/cross_plus.gif",
bL="decoration/arrows/down-small.gif",
bM="datechooser-week",
bN="icon/16/apps/office-calendar.png",
bO="datechooser-weekday",
bP="decoration/treevirtual/end.gif",
bQ="table-header-cell-hover",
bR="window-active-caption-text",
bS="window-active-caption",
bT="icon",
bU="checkbox-checked-focused",
bV="toolbar-separator",
bW="groove",
bX="checkbox-pressed",
bY="decoration/window/restore.gif",
ca="decoration/menu/checkbox-invert.gif",
cb="scrollarea",
cc="window-inactive-caption-text",
cd="button-checked",
ce="up.gif",
cf="tabview-page-button-right",
cg="checkbox-disabled",
ch="tabview-page-button-top",
ci="tabview-page-button-bottom",
cj="decoration/menu/radiobutton.gif",
ck="decoration/arrows/",
cl="decoration/table/descending.png",
cm="tooltip-text",
cn="checkbox-checked-hovered",
co="left.gif",
cp="alias",
cq="checkbox-checked-disabled",
cr="decoration/arrows/right-invert.gif",
cs="radiobutton-checked-disabled",
ct="lead-item",
cu="checkbox-focused",
cv="border-dark",
cw="decoration/treevirtual/end_plus.gif",
cx="decoration/treevirtual/start_minus.gif",
cy="radiobutton-checked-hovered",
cz="decoration/window/minimize.gif",
cA="checkbox-checked",
cB="table-header-cell-hovered",
cC="down.gif",
cD="decoration/treevirtual/cross_minus.gif",
cE="decoration/treevirtual/end_minus.gif",
cF="left",
cG="window-inactive-caption",
cH="decoration/menu/radiobutton-invert.gif",
cI="slider",
cJ="decoration/table/select-column-order.png",
cK="decoration/arrows/next.gif",
cL="decoration/treevirtual/only_minus.gif",
cM="datechooser-week-header",
cN="decoration/window/maximize.gif",
cO="decoration/treevirtual/only_plus.gif",
cP="checkbox-checked-pressed",
cQ="menu-separator",
cR="decoration/splitpane/knob-vertical.png",
cS=".gif",
cT="decoration/arrows/forward.gif",
cU="radiobutton-checked-pressed",
cV="table-statusbar",
cW="radiobutton-pressed",
cX="copy",
cY="radiobutton-focused",
da="decoration/splitpane/knob-horizontal.png",
db="right.gif",
dc="radiobutton-checked",
dd="decoration/treevirtual/cross.gif",
de="decoration/table/ascending.png",
df="decoration/treevirtual/line.gif",
dg="table-header",
dh="date-chooser-selected",
di="toolbar-part-handle",
dj="decoration/window/close.gif",
dk="icon/16/actions/view-refresh.png";
qx.Theme.define(bz,
{appearances:{"widget":{},
"label":{style:function(dl){return {textColor:dl.disabled?o:b};
}},
"image":{style:function(dl){return {opacity:!dl.replacement&&dl.disabled?0.3:b};
}},
"atom":{},
"atom/label":E,
"atom/icon":U,
"root":{style:function(dl){return {backgroundColor:c,
textColor:bH,
font:H};
}},
"popup":{style:function(dl){return {decorator:A,
backgroundColor:bJ,
shadow:v};
}},
"tooltip":{include:r,
style:function(dl){return {backgroundColor:n,
textColor:cm,
decorator:n,
shadow:v,
padding:[1,
3,
2,
3],
offset:[1,
1,
20,
1]};
}},
"tooltip/atom":f,
"iframe":{style:function(dl){return {backgroundColor:J,
decorator:h};
}},
"move-frame":{style:function(dl){return {decorator:A};
}},
"resize-frame":bs,
"dragdrop-cursor":{style:function(dl){var dm=bt;
if(dl.copy){dm=cX;
}else if(dl.move){dm=bw;
}else if(dl.alias){dm=cp;
}return {source:bp+dm+cS,
position:ba,
offset:[2,
16,
2,
6]};
}},
"button":{alias:f,
style:function(dl){if(dl.pressed||dl.abandoned||dl.checked){var dn=!dl.inner&&dl.focused?j:h;
}else{var dn=!dl.inner&&dl.focused?bF:g;
}
if(dl.pressed||dl.abandoned||dl.checked){var dp=[4,
3,
2,
5];
}else{var dp=[3,
4];
}return {backgroundColor:dl.abandoned?N:dl.hovered?u:dl.checked?cd:a,
decorator:dn,
padding:dp};
}},
"splitbutton":{},
"splitbutton/button":a,
"splitbutton/arrow":{alias:a,
include:a,
style:function(dl){return {icon:z};
}},
"scrollarea/corner":{style:function(){return {backgroundColor:c,
width:0,
height:0};
}},
"scrollarea":d,
"scrollarea/pane":d,
"scrollarea/scrollbar-x":w,
"scrollarea/scrollbar-y":w,
"list":{alias:cb,
style:function(dl){return {decorator:dl.focused?j:h,
backgroundColor:dl.focused?L:J};
}},
"listitem":{alias:f,
style:function(dl){return {gap:4,
padding:dl.lead?[2,
4]:[3,
5],
backgroundColor:dl.selected?B:b,
textColor:dl.selected?q:b,
decorator:dl.lead?ct:b};
}},
"textfield":{style:function(dl){return {decorator:dl.focused?j:h,
padding:[2,
3],
textColor:dl.disabled?o:b,
backgroundColor:dl.disabled?Q:dl.focused?L:P};
}},
"textarea":s,
"checkbox":{alias:f,
style:function(dl){var dm;
if(dl.checked&&dl.focused){dm=bU;
}else if(dl.checked&&dl.disabled){dm=cq;
}else if(dl.checked&&dl.pressed){dm=cP;
}else if(dl.checked&&dl.hovered){dm=cn;
}else if(dl.checked){dm=cA;
}else if(dl.disabled){dm=cg;
}else if(dl.focused){dm=cu;
}else if(dl.pressed){dm=bX;
}else if(dl.hovered){dm=bG;
}else{dm=p;
}return {icon:bh+dm+bb,
gap:6};
}},
"radiobutton":{alias:p,
include:p,
style:function(dl){var dm;
if(dl.checked&&dl.focused){dm=bx;
}else if(dl.checked&&dl.disabled){dm=cs;
}else if(dl.checked&&dl.pressed){dm=cU;
}else if(dl.checked&&dl.hovered){dm=cy;
}else if(dl.checked){dm=dc;
}else if(dl.disabled){dm=bD;
}else if(dl.focused){dm=cY;
}else if(dl.pressed){dm=cW;
}else if(dl.hovered){dm=bn;
}else{dm=G;
}return {icon:bh+dm+bb};
}},
"spinner":{style:function(dl){return {decorator:dl.focused?j:h,
textColor:dl.disabled?o:b};
}},
"spinner/textfield":{include:s,
style:function(dl){return {decorator:b,
padding:[2,
3]};
}},
"spinner/upbutton":{alias:a,
include:a,
style:function(dl){return {icon:bv,
padding:dl.pressed?[2,
2,
0,
4]:[1,
3,
1,
3],
backgroundColor:dl.hovered?u:a};
}},
"spinner/downbutton":{alias:a,
include:a,
style:function(dl){return {icon:bL,
padding:dl.pressed?[2,
2,
0,
4]:[1,
3,
1,
3],
backgroundColor:dl.hovered?u:a};
}},
"datefield":K,
"datefield/button":{alias:S,
include:S,
style:function(dl){return {icon:bN,
padding:[0,
3],
backgroundColor:dl.disabled?Q:dl.focused?L:P,
decorator:b};
}},
"datefield/list":{alias:bc,
include:bc,
style:function(dl){return {decorator:dl.focused?j:h};
}},
"groupbox":{style:function(dl){return {backgroundColor:c};
}},
"groupbox/legend":{alias:f,
style:function(dl){return {backgroundColor:c,
paddingRight:4,
paddingLeft:4,
marginRight:10,
marginLeft:10};
}},
"groupbox/frame":{style:function(dl){return {padding:[12,
9],
decorator:bW};
}},
"check-groupbox":i,
"check-groupbox/legend":{alias:p,
include:p,
style:function(dl){return {backgroundColor:c,
paddingRight:3,
paddingLeft:3,
marginRight:10,
marginLeft:10};
}},
"radio-groupbox":i,
"radio-groupbox/legend":{alias:G,
include:G,
style:function(dl){return {backgroundColor:c,
paddingRight:3,
paddingLeft:3,
marginRight:10,
marginLeft:10};
}},
"toolbar":{style:function(dl){return {decorator:D,
backgroundColor:c};
}},
"toolbar/part":{},
"toolbar/part/container":{},
"toolbar/part/handle":{style:function(dl){return {decorator:di,
backgroundColor:c,
width:4,
margin:[3,
2],
allowGrowY:true};
}},
"toolbar-separator":{style:function(dl){return {width:1,
margin:[3,
2],
decorator:bV};
}},
"toolbar-button":{alias:f,
style:function(dl){if(dl.pressed||dl.checked||dl.abandoned){var dq=e;
var dp=[3,
2,
1,
4];
}else if(dl.hovered){var dq=D;
var dp=[2,
3];
}else{var dq=b;
var dp=[3,
4];
}return {cursor:H,
decorator:dq,
padding:dp,
backgroundColor:dl.abandoned?N:dl.checked?O:a};
}},
"toolbar-splitbutton":{},
"toolbar-splitbutton/button":M,
"toolbar-splitbutton/arrow":{alias:M,
include:M,
style:function(dl){return {icon:z};
}},
"slidebar":{},
"slidebar/scrollpane":{},
"slidebar/content":{},
"slidebar/button-forward":{alias:a,
include:a,
style:function(dl){return {icon:cK};
}},
"slidebar/button-backward":{alias:a,
include:a,
style:function(dl){return {icon:bl};
}},
"tabview":{},
"tabview/bar":{alias:bq,
style:function(dl){var dr=0,
ds=0,
dt=0,
du=0;
if(dl.barTop){dt=-2;
}else if(dl.barBottom){dr=-2;
}else if(dl.barRight){du=-2;
}else{ds=-2;
}return {marginBottom:dt,
marginTop:dr,
marginLeft:du,
marginRight:ds};
}},
"tabview/bar/button-forward":{include:bf,
alias:bf,
style:function(dl){if(dl.barTop||dl.barBottom){return {marginTop:2,
marginBottom:2};
}else{return {marginLeft:2,
marginRight:2};
}}},
"tabview/bar/button-backward":{include:bd,
alias:bd,
style:function(dl){if(dl.barTop||dl.barBottom){return {marginTop:2,
marginBottom:2};
}else{return {marginLeft:2,
marginRight:2};
}}},
"tabview/pane":{style:function(dl){return {backgroundColor:c,
decorator:g,
padding:10};
}},
"tabview-page":{},
"tabview-page/button":{alias:a,
style:function(dl){var dn;
var dr=0,
ds=0,
dt=0,
du=0;
if(dl.barTop||dl.barBottom){var dv=2,
dw=2,
dx=6,
dy=6;
}else{var dv=6,
dw=6,
dx=6,
dy=6;
}
if(dl.barTop){dn=ch;
}else if(dl.barRight){dn=cf;
}else if(dl.barBottom){dn=ci;
}else{dn=bu;
}
if(dl.checked){if(dl.barTop||dl.barBottom){dx+=2;
dy+=2;
}else{dv+=2;
dw+=2;
}}else{if(dl.barTop||dl.barBottom){dt+=2;
dr+=2;
}else if(dl.barLeft||dl.barRight){ds+=2;
du+=2;
}}
if(dl.checked){if(!dl.firstTab){if(dl.barTop||dl.barBottom){du=-4;
}else{dr=-4;
}}
if(!dl.lastTab){if(dl.barTop||dl.barBottom){ds=-4;
}else{dt=-4;
}}}return {zIndex:dl.checked?10:5,
decorator:dn,
backgroundColor:c,
iconPosition:dl.barLeft||dl.barRight?bm:cF,
padding:[dv,
dy,
dw,
dx],
margin:[dr,
ds,
dt,
du]};
}},
"scrollbar":{},
"scrollbar/slider":{alias:cI,
style:function(dl){return {backgroundColor:O};
}},
"scrollbar/slider/knob":{include:a,
style:function(dl){return {height:14,
width:14,
minHeight:dl.horizontal?b:14,
minWidth:dl.horizontal?14:b};
}},
"scrollbar/button":{alias:a,
include:a,
style:function(dl){var dp;
if(dl.up||dl.down){if(dl.pressed||dl.abandoned||dl.checked){dp=[5,
2,
3,
4];
}else{dp=[4,
3];
}}else{if(dl.pressed||dl.abandoned||dl.checked){dp=[4,
3,
2,
5];
}else{dp=[3,
4];
}}var dm=ck;
if(dl.left){dm+=co;
}else if(dl.right){dm+=db;
}else if(dl.up){dm+=ce;
}else{dm+=cC;
}return {padding:dp,
icon:dm};
}},
"scrollbar/button-begin":R,
"scrollbar/button-end":R,
"slider":{style:function(dl){return {backgroundColor:O,
decorator:dl.focused?j:h};
}},
"slider/knob":{include:a,
style:function(dl){return {width:14,
height:14,
decorator:g};
}},
"tree-folder/open":{style:function(dl){return {source:dl.opened?bi:bk};
}},
"tree-folder":{style:function(dl){return {padding:[2,
3,
2,
0],
icon:dl.opened?bj:W};
}},
"tree-folder/icon":{style:function(dl){return {padding:[0,
4,
0,
0]};
}},
"tree-folder/label":{style:function(dl){return {padding:[1,
2],
backgroundColor:dl.selected?B:b,
textColor:dl.selected?q:b};
}},
"tree-file":{include:X,
alias:X,
style:function(dl){return {icon:Y};
}},
"tree":{include:t,
alias:t,
style:function(dl){return {contentPadding:[4,
4,
4,
4]};
}},
"treevirtual":{style:function(dl){return {decorator:A};
}},
"treevirtual-folder":{style:function(dl){return {icon:(dl.opened?bj:W)};
}},
"treevirtual-file":{include:bg,
alias:bg,
style:function(dl){return {icon:Y};
}},
"treevirtual-line":{style:function(dl){return {icon:df};
}},
"treevirtual-contract":{style:function(dl){return {icon:bi};
}},
"treevirtual-expand":{style:function(dl){return {icon:bk};
}},
"treevirtual-only-contract":{style:function(dl){return {icon:cL};
}},
"treevirtual-only-expand":{style:function(dl){return {icon:cO};
}},
"treevirtual-start-contract":{style:function(dl){return {icon:cx};
}},
"treevirtual-start-expand":{style:function(dl){return {icon:bo};
}},
"treevirtual-end-contract":{style:function(dl){return {icon:cE};
}},
"treevirtual-end-expand":{style:function(dl){return {icon:cw};
}},
"treevirtual-cross-contract":{style:function(dl){return {icon:cD};
}},
"treevirtual-cross-expand":{style:function(dl){return {icon:bK};
}},
"treevirtual-end":{style:function(dl){return {icon:bP};
}},
"treevirtual-cross":{style:function(dl){return {icon:dd};
}},
"window":{style:function(dl){return {contentPadding:[10,
10,
10,
10],
backgroundColor:c,
decorator:dl.maximized?b:g,
shadow:v};
}},
"window/pane":{},
"window/captionbar":{style:function(dl){return {padding:1,
backgroundColor:dl.active?bS:cG,
textColor:dl.active?bR:cc};
}},
"window/icon":{style:function(dl){return {marginRight:4};
}},
"window/title":{style:function(dl){return {cursor:H,
font:I,
marginRight:20,
alignY:k};
}},
"window/minimize-button":{include:a,
alias:a,
style:function(dl){return {icon:cz,
padding:dl.pressed||dl.abandoned?[2,
1,
0,
3]:[1,
2]};
}},
"window/restore-button":{include:a,
alias:a,
style:function(dl){return {icon:bY,
padding:dl.pressed||dl.abandoned?[2,
1,
0,
3]:[1,
2]};
}},
"window/maximize-button":{include:a,
alias:a,
style:function(dl){return {icon:cN,
padding:dl.pressed||dl.abandoned?[2,
1,
0,
3]:[1,
2]};
}},
"window/close-button":{include:a,
alias:a,
style:function(dl){return {marginLeft:2,
icon:dj,
padding:dl.pressed||dl.abandoned?[2,
1,
0,
3]:[1,
2]};
}},
"window/statusbar":{style:function(dl){return {decorator:e,
padding:[2,
6]};
}},
"window/statusbar-text":E,
"resizer":{style:function(dl){return {decorator:g};
}},
"splitpane":{},
"splitpane/splitter":{style:function(dl){return {backgroundColor:c};
}},
"splitpane/splitter/knob":{style:function(dl){return {source:dl.horizontal?da:cR,
padding:2};
}},
"splitpane/slider":{style:function(dl){return {backgroundColor:cv,
opacity:0.3};
}},
"selectbox":a,
"selectbox/atom":f,
"selectbox/popup":r,
"selectbox/list":t,
"selectbox/arrow":{style:function(dl){return {source:z,
paddingRight:4,
paddingLeft:5};
}},
"datechooser":{style:function(dl){return {decorator:g};
}},
"datechooser/navigation-bar":{style:function(dl){return {backgroundColor:C,
padding:[2,
10]};
}},
"datechooser/last-year-button-tooltip":n,
"datechooser/last-month-button-tooltip":n,
"datechooser/next-year-button-tooltip":n,
"datechooser/next-month-button-tooltip":n,
"datechooser/last-year-button":y,
"datechooser/last-month-button":y,
"datechooser/next-year-button":y,
"datechooser/next-month-button":y,
"datechooser/button/icon":{},
"datechooser/button":{style:function(dl){var dz={width:17,
show:bT};
if(dl.lastYear){dz.icon=bB;
}else if(dl.lastMonth){dz.icon=bl;
}else if(dl.nextYear){dz.icon=cT;
}else if(dl.nextMonth){dz.icon=V;
}
if(dl.pressed||dl.checked||dl.abandoned){dz.decorator=e;
}else if(dl.hovered){dz.decorator=D;
}else{dz.decorator=b;
}
if(dl.pressed||dl.checked||dl.abandoned){dz.padding=[2,
0,
0,
2];
}else if(dl.hovered){dz.padding=1;
}else{dz.padding=2;
}return dz;
}},
"datechooser/month-year-label":{style:function(dl){return {font:I,
textAlign:x};
}},
"datechooser/date-pane":{style:function(dl){return {decorator:bI,
backgroundColor:C};
}},
"datechooser-weekday":{style:function(dl){return {decorator:bO,
font:I,
textAlign:x,
textColor:dl.weekend?F:C,
backgroundColor:dl.weekend?C:F};
}},
"datechooser-day":{style:function(dl){return {textAlign:x,
decorator:dl.today?A:b,
textColor:dl.selected?q:dl.otherMonth?o:b,
backgroundColor:dl.selected?dh:b,
padding:[2,
4]};
}},
"datechooser-week":{style:function(dl){return {textAlign:x,
textColor:F,
padding:[2,
4],
decorator:dl.header?cM:bM};
}},
"combobox":{style:function(dl){return {decorator:dl.focused?j:h,
textColor:dl.disabled?o:b,
backgroundColor:P};
}},
"combobox/button":{alias:a,
include:a,
style:function(dl){return {icon:z,
backgroundColor:dl.hovered?u:a};
}},
"combobox/popup":r,
"combobox/list":t,
"combobox/textfield":{include:s,
style:function(dl){return {decorator:b,
padding:[2,
3]};
}},
"menu":{style:function(dl){var dz={backgroundColor:c,
shadow:v,
decorator:g,
spacingX:6,
spacingY:1,
iconColumnWidth:16,
arrowColumnWidth:4,
padding:1};
if(dl.submenu){dz.position=ba;
dz.offset=[-2,
-3];
}
if(dl.contextmenu){dz.offset=4;
}return dz;
}},
"menu-separator":{style:function(dl){return {height:0,
decorator:cQ,
marginTop:4,
marginBottom:4,
marginLeft:2,
marginRight:2};
}},
"menu-button":{alias:f,
style:function(dl){return {backgroundColor:dl.selected?B:b,
textColor:dl.selected?q:b,
padding:[2,
6]};
}},
"menu-button/icon":{include:U,
style:function(dl){return {alignY:k};
}},
"menu-button/label":{include:E,
style:function(dl){return {alignY:k,
padding:1};
}},
"menu-button/shortcut":{include:E,
style:function(dl){return {alignY:k,
marginLeft:14,
padding:1};
}},
"menu-button/arrow":{style:function(dl){return {source:dl.selected?cr:V,
alignY:k};
}},
"menu-checkbox":{alias:l,
include:l,
style:function(dl){return {icon:!dl.checked?b:dl.selected?ca:bA};
}},
"menu-radiobutton":{alias:l,
include:l,
style:function(dl){return {icon:!dl.checked?b:dl.selected?cH:cj};
}},
"menubar":{style:function(dl){return {backgroundColor:c,
decorator:g};
}},
"menubar-button":{alias:f,
style:function(dl){return {padding:[2,
6],
backgroundColor:dl.pressed||dl.hovered?B:b,
textColor:dl.pressed||dl.hovered?q:b};
}},
"colorselector":d,
"colorselector/control-bar":d,
"colorselector/visual-pane":i,
"colorselector/control-pane":d,
"colorselector/preset-grid":d,
"colorselector-colorbucket":{style:function(dl){return {decorator:e,
width:16,
height:16};
}},
"colorselector/preset-field-set":i,
"colorselector/input-field-set":i,
"colorselector/preview-field-set":i,
"colorselector/hex-field-composite":d,
"colorselector/hex-field":s,
"colorselector/rgb-spinner-composite":d,
"colorselector/rgb-spinner-red":m,
"colorselector/rgb-spinner-green":m,
"colorselector/rgb-spinner-blue":m,
"colorselector/hsb-spinner-composite":d,
"colorselector/hsb-spinner-hue":m,
"colorselector/hsb-spinner-saturation":m,
"colorselector/hsb-spinner-brightness":m,
"colorselector/preview-content-old":{style:function(dl){return {decorator:e,
width:50,
height:10};
}},
"colorselector/preview-content-new":{style:function(dl){return {decorator:e,
backgroundColor:J,
width:50,
height:10};
}},
"colorselector/hue-saturation-field":{style:function(dl){return {decorator:e,
margin:5};
}},
"colorselector/brightness-field":{style:function(dl){return {decorator:e,
margin:[5,
7]};
}},
"colorselector/hue-saturation-pane":d,
"colorselector/hue-saturation-handle":d,
"colorselector/brightness-pane":d,
"colorselector/brightness-handle":d,
"table":d,
"table/statusbar":{style:function(dl){return {decorator:cV,
paddingLeft:2,
paddingRight:2};
}},
"table/column-button":{alias:a,
style:function(dl){var dq,
dp;
if(dl.pressed||dl.checked||dl.abandoned){dq=e;
dp=[3,
2,
1,
4];
}else if(dl.hovered){dq=D;
dp=[2,
3];
}else{dq=b;
dp=[3,
4];
}return {decorator:dq,
padding:dp,
backgroundColor:dl.abandoned?N:a,
icon:cJ};
}},
"table-column-reset-button":{extend:l,
alias:l,
style:function(){return {icon:dk};
}},
"table-scroller/scrollbar-x":w,
"table-scroller/scrollbar-y":w,
"table-scroller":d,
"table-scroller/header":{style:function(dl){return {decorator:bC,
backgroundColor:dg};
}},
"table-scroller/pane":{style:function(dl){return {backgroundColor:bE};
}},
"table-scroller/focus-indicator":{style:function(dl){return {decorator:br};
}},
"table-scroller/resize-line":{style:function(dl){return {backgroundColor:by,
width:3};
}},
"table-header-cell":{alias:f,
style:function(dl){return {paddingLeft:2,
paddingRight:2,
paddingBottom:dl.hovered?0:2,
decorator:dl.hovered?cB:T,
backgroundColor:dl.hovered?bQ:T,
sortIcon:dl.sorted?(dl.sortedAscending?de:cl):b};
}},
"table-header-cell/sort-icon":{style:function(dl){return {alignY:k};
}},
"table-editor-textfield":{include:s,
style:function(dl){return {decorator:b,
padding:[2,
2]};
}},
"table-editor-selectbox":{include:be,
alias:be,
style:function(dl){return {padding:[0,
2]};
}},
"table-editor-combobox":{include:K,
alias:K,
style:function(dl){return {decorator:b};
}},
"colorpopup":{alias:r,
include:r,
style:function(dl){return {decorator:g,
padding:5,
backgroundColor:c};
}},
"colorpopup/field":{style:function(dl){return {decorator:e,
margin:2,
width:14,
height:14,
backgroundColor:c};
}},
"colorpopup/selector-button":a,
"colorpopup/auto-button":a,
"colorpopup/preview-pane":i,
"colorpopup/current-preview":{style:function(dA){return {height:20,
padding:4,
marginLeft:4,
decorator:e,
allowGrowX:true};
}},
"colorpopup/selected-preview":{style:function(dA){return {height:20,
padding:4,
marginRight:4,
decorator:e,
allowGrowX:true};
}}}});
})();
(function(){var a="Oxygen",
b="qx.theme.icon.Oxygen",
c="qx/icon/Oxygen";
qx.Theme.define(b,
{title:a,
resource:c,
icons:{}});
})();
(function(){var a="Classic Windows",
b="qx.theme.Classic";
qx.Theme.define(b,
{title:a,
meta:{color:qx.theme.classic.Color,
decoration:qx.theme.classic.Decoration,
font:qx.theme.classic.Font,
appearance:qx.theme.classic.Appearance,
icon:qx.theme.icon.Oxygen}});
})();
(function(){var a='px;',
b="blank.gif",
c="",
d="Boolean",
e='px',
g='</div>',
h='<div style="position:absolute;',
j='left:',
k=";",
l="treevirtual-start-contract",
m='',
n="treevirtual-file",
p="qx.ui.treevirtual.SimpleTreeDataCellRenderer",
q="qx.client",
r='height:',
s='right:',
t="  src='",
u='" title="',
v="treevirtual-end",
w="treevirtual-cross",
x="treevirtual-cross-contract",
y="__lM",
z="treevirtual-end-contract",
A='<img src="',
B="__lO",
C=';width:',
D="treevirtual-end-expand",
E="treevirtual-line",
F='bottom:',
G=';">',
H="progid:DXImageTransform.Microsoft.AlphaImageLoader(",
I='">',
J='top:',
K=';height:',
L="treevirtual-only-contract",
M="treevirtual-start-expand",
N="mshtml",
O='"/>',
P='" style="',
Q='top:0',
R="treevirtual-folder",
S="STATIC_URI",
T="ImageLoader",
U="treevirtual-contract",
V='width:',
W="treevirtual-expand",
X="treevirtual-cross-expand",
Y="treevirtual-only-expand",
ba='" style="filter:',
bb="__lN",
bc="',sizingMethod='scale')";
qx.Class.define(p,
{extend:qx.ui.table.cellrenderer.Abstract,
construct:function(){arguments.callee.base.call(this);
this.__lM=qx.util.AliasManager.getInstance();
this.__lN=qx.util.ResourceManager;
this.__lO=qx.theme.manager.Appearance.getInstance();
this.STATIC_URI=this.__lN.toUri(this.__lM.resolve("static/"));
this.ImageLoader=qx.io2.ImageLoader;
},
statics:{__lP:{}},
properties:{useTreeLines:{check:d,
init:true},
excludeFirstLevelTreeLines:{check:d,
init:false},
alwaysShowOpenCloseSymbol:{check:d,
init:false}},
members:{useTreeLines:function(){return this.getUseTreeLines();
},
_getCellStyle:function(bd){var be=bd.value;
var bf=arguments.callee.base.call(this,
bd)+(be.cellStyle?be.cellStyle+k:c);
return bf;
},
__lQ:function(bg){var bf=[];
var bh=this.__lN.toUri(this.__lM.resolve(bg.url));
if(bg.position){var bi=bg.position;
bf.push(h);
if(bi.top!==undefined){bf.push(J+bi.top+a);
}
if(bi.right!==undefined){bf.push(s+bi.right+a);
}
if(bi.bottom!==undefined){bf.push(F+bi.bottom+a);
}
if(bi.left!==undefined){bf.push(j+bi.left+a);
}
if(bi.width!==undefined){bf.push(V+bi.width+a);
}
if(bi.height!==undefined){bf.push(r+bi.height+a);
}bf.push(I);
}bf.push(A);
if(qx.core.Variant.isSet(q,
N)&&/\.png$/i.test(bg.url)){bf.push(this.STATIC_URI+b+ba+H+t+bh+bc);
}else{bf.push(bh+P);
}
if(bg.imageWidth&&bg.imageHeight){bf.push(C+bg.imageWidth+e+K+bg.imageHeight+e);
}var bj=bg.tooltip;
if(bj!=null){bf.push(u+bj);
}bf.push(O);
if(bg.position){bf.push(g);
}return bf.join(c);
},
_addExtraContentBeforeIcon:function(bd){return {html:m,
width:0};
},
_getContentHtml:function(bd){var bf=c;
var be=bd.value;
var bk;
var bl=this.getUseTreeLines();
var bm=this.getExcludeFirstLevelTreeLines();
var bn=this.getAlwaysShowOpenCloseSymbol();
var bi=0;
for(var bo=0;bo<be.level;bo++){bk=this._getIndentSymbol(bo,
be,
bl,
bn,
bm);
bf+=this.__lQ({url:bk.icon,
position:{top:0+(bk.paddingTop||0),
left:bi+(bk.paddingLeft||0),
width:19,
height:16},
imageWidth:8,
imageHeight:5});
bi+=19;
}var bp=this._addExtraContentBeforeIcon(bd);
bf+=bp.html;
bi+=bp.width;
var bq=(be.bSelected?be.iconSelected:be.icon);
if(!bq){if(be.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF){var br=this.__lO.styleFrom(n);
}else{var bs={opened:be.bOpened};
var br=this.__lO.styleFrom(R,
bs);
}bq=br.icon;
}bf+=this.__lQ({url:bq,
position:{top:0,
left:bi,
width:19,
height:16},
imageWidth:16,
imageHeight:16});
bf+=h+j+((be.level*19)+16+2+2)+a+Q+(be.labelStyle?k+be.labelStyle:c)+G+be.label+g;
return bf;
},
_getIndentSymbol:function(bt,
be,
bl,
bn,
bm){var bu=qx.ui.treevirtual.SimpleTreeDataCellRenderer;
if(bt==0&&bm){bl=false;
}if(bt<be.level-1){return (bl&&!be.lastChild[bt]?bu.__lP.line:{icon:this.STATIC_URI+b});
}var bv=be.lastChild[be.lastChild.length-1];
if(be.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH&&!be.bHideOpenClose){if(be.children.length>0||bn){if(!bl){return (be.bOpened?bu.__lP.contract:bu.__lP.expand);
}if(bt==0&&be.bFirstChild){if(bv){return (be.bOpened?bu.__lP.onlyContract:bu.__lP.onlyExpand);
}else{return (be.bOpened?bu.__lP.startContract:bu.__lP.startExpand);
}}if(bv){return (be.bOpened?bu.__lP.endContract:bu.__lP.endExpand);
}return (be.bOpened?bu.__lP.crossContract:bu.__lP.crossExpand);
}}if(bl){if(be.parentNodeId==0){if(bv&&be.bFirstChild){return {icon:this.STATIC_URI+b};
}if(bv){return bu.__lP.end;
}if(be.bFirstChild){return bu.__lP.start;
}}return (bv?bu.__lP.end:bu.__lP.cross);
}return {icon:this.STATIC_URI+b};
}},
defer:function(){qx.theme.manager.Meta.getInstance().initialize();
var bu=qx.ui.treevirtual.SimpleTreeDataCellRenderer;
var bw=qx.io2.ImageLoader;
var bx=qx.util.AliasManager.getInstance();
var by=qx.util.ResourceManager;
var bz=qx.theme.manager.Appearance.getInstance();
var bA=function(bB){bw.load(by.toUri(bx.resolve(bB)));
};
bu.__lP.line=bz.styleFrom(E);
bA(bu.__lP.line.icon);
bu.__lP.contract=bz.styleFrom(U);
bA(bu.__lP.contract.icon);
bu.__lP.expand=bz.styleFrom(W);
bA(bu.__lP.expand.icon);
bu.__lP.onlyContract=bz.styleFrom(L);
bA(bu.__lP.onlyContract.icon);
bu.__lP.onlyExpand=bz.styleFrom(Y);
bA(bu.__lP.onlyExpand.icon);
bu.__lP.startContract=bz.styleFrom(l);
bA(bu.__lP.startContract.icon);
bu.__lP.startExpand=bz.styleFrom(M);
bA(bu.__lP.startExpand.icon);
bu.__lP.endContract=bz.styleFrom(z);
bA(bu.__lP.endContract.icon);
bu.__lP.endExpand=bz.styleFrom(D);
bA(bu.__lP.endExpand.icon);
bu.__lP.crossContract=bz.styleFrom(x);
bA(bu.__lP.crossContract.icon);
bu.__lP.crossExpand=bz.styleFrom(X);
bA(bu.__lP.crossExpand.icon);
bu.__lP.end=bz.styleFrom(v);
bA(bu.__lP.end.icon);
bu.__lP.cross=bz.styleFrom(w);
bA(bu.__lP.cross.icon);
},
destruct:function(){this._disposeFields(y,
bb,
B,
S,
T);
}});
})();
(function(){var a="qx.ui.treevirtual.DefaultDataCellRenderer";
qx.Class.define(a,
{extend:qx.ui.table.cellrenderer.Default,
construct:function(){arguments.callee.base.call(this);
},
members:{_getCellStyle:function(b){var c=arguments.callee.base.call(this,
b)+qx.ui.treevirtual.SimpleTreeDataCellRenderer.MAIN_DIV_STYLE;
return c;
}}});
})();
(function(){var a="qx.ui.treevirtual.SimpleTreeDataRowRenderer";
qx.Class.define(a,
{extend:qx.ui.table.rowrenderer.Default,
construct:function(){arguments.callee.base.call(this);
},
members:{updateDataRowElement:function(b,
c){var d=b.table;
var e=b.rowData;
var f=d.getTableModel();
var g=f.getTreeColumn();
var h=e[g];
b.selected=h.bSelected;
if(h.bSelected){var i=b.row;
d.getSelectionModel()._addSelectionInterval(i,
i);
}arguments.callee.base.call(this,
b,
c);
}}});
})();
(function(){var a="qx.ui.treevirtual.SelectionManager",
b="_table",
c="Space",
d="Enter";
qx.Class.define(a,
{extend:qx.ui.table.selection.Manager,
construct:function(e){arguments.callee.base.call(this);
this._table=e;
},
members:{handleMoveKeyDown:function(f,
g){var h=this.getSelectionModel();
switch(g.getModifiers()){case 0:break;
case qx.event.type.Dom.SHIFT_MASK:var j=h.getAnchorSelectionIndex();
if(j==-1){h.setSelectionInterval(f,
f);
}else{h.setSelectionInterval(j,
f);
}break;
}},
_handleSelectEvent:function(f,
g){function k(e,
f,
g){var l=e.getTableModel().getTreeColumn();
var m=e.getTableModel();
if(g instanceof qx.event.type.Mouse){if(!e.getFocusCellOnMouseMove()){var n=e._getPaneScrollerArr();
for(var o=0;o<n.length;o++){n[o]._focusCellAtPagePos(g.getPageX(),
g.getPageY());
}}}var p=m.getValue(l,
e.getFocusedRow());
if(!p){return false;
}if(g instanceof qx.event.type.Mouse){var q=e.getTableColumnModel();
var r=q._getColToXPosMap();
var s=qx.bom.element.Location.getLeft(e.getContentElement().getDomElement());
for(o=0;o<r[l].visX;o++){s+=q.getColumnWidth(r[o].visX);
}var t=g.getViewportLeft();
var u=2;
var v=s+(p.level-1)*19+2;
if(t>=v-u&&t<=v+19+u){m.setState(p,
{bOpened:!p.bOpened});
return e.getOpenCloseClickSelectsRow()?false:true;
}else{return false;
}}else{var w=g.getKeyIdentifier();
switch(w){case c:return false;
case d:if(!p.bHideOpenClose){m.setState(p,
{bOpened:!p.bOpened});
}return e.getOpenCloseClickSelectsRow()?false:true;
default:return true;
}}}var y=k(this._table,
f,
g);
if(!y){var z=qx.ui.table.selection.Manager;
z.prototype._handleSelectEvent.call(this,
f,
g);
}}},
destruct:function(){this._disposeFields(b);
}});
})();
(function(){var a="number",
c="object",
d="qx.ui.treevirtual.MNode";
qx.Mixin.define(d,
{members:{nodeGet:function(e){if(typeof (e)==c){return e;
}else if(typeof (e)==a){return this.getTableModel().getData()[e];
}else{throw new Error("Expected node object or node id");
}},
nodeToggleOpened:function(e){var f;
var g;
if(typeof (e)==c){f=e;
g=f.nodeId;
}else if(typeof (e)==a){g=e;
f=this.getTableModel().getData()[g];
}else{throw new Error("Expected node object or node id");
}this.getTableModel().setState(g,
{bOpened:!f.bOpened});
},
nodeSetState:function(e,
h){var g;
if(typeof (e)==c){g=e.nodeId;
}else if(typeof (e)==a){g=e;
}else{throw new Error("Expected node object or node id");
}this.getTableModel().setState(g,
h);
},
nodeSetLabel:function(e,
i){this.nodeSetState(e,
{label:i});
},
nodeGetLabel:function(e){var f=this.nodeGet(e);
return f.label;
},
nodeSetSelected:function(e,
j){this.nodeSetState(e,
{bSelected:j});
},
nodeGetSelected:function(e){var f=this.nodeGet(e);
return f.bSelected;
},
nodeSetOpened:function(e,
j){var f;
if(typeof (e)==c){f=e;
}else if(typeof (e)==a){f=this.getTableModel().getData()[e];
}else{throw new Error("Expected node object or node id");
}if(j!=f.bOpened){this.nodeToggleOpened(f);
}},
nodeGetOpened:function(e){var f=this.nodeGet(e);
return f.bOpened;
},
nodeSetHideOpenClose:function(e,
j){this.nodeSetState(e,
{bHideOpenClose:j});
},
nodeGetHideOpenClose:function(e){var f=this.nodeGet(e);
return f.bHideOpenClose;
},
nodeSetIcon:function(e,
k){this.nodeSetState(e,
{icon:k});
},
nodeGetIcon:function(e){var f=this.nodeGet(e);
return f.icon;
},
nodeSetSelectedIcon:function(e,
k){this.nodeSetState(e,
{iconSelected:k});
},
nodeGetSelectedIcon:function(e){var f=this.nodeGet(e);
return f.iconSelected;
},
nodeSetCellStyle:function(e,
l){this.nodeSetState(e,
{cellStyle:l});
},
nodeGetCellStyle:function(e){var f=this.nodeGet(e);
return f.cellStyle;
},
nodeSetLabelStyle:function(e,
l){this.nodeSetState(e,
{labelStyle:l});
},
nodeGetLabelStyle:function(e){var f=this.nodeGet(e);
return f.cellStyle;
}}});
})();
(function(){var a='px;',
b="",
c='',
d='px',
e='</div>',
g='<div style="position:absolute;',
h='">',
j='left:',
k=";",
l='</span>',
m='background-image:url(',
n="treevirtual-only-expand",
p="treevirtual-start-contract",
q='">&nbsp;</div>',
r="__lS",
s="treevirtual-file",
t="qx.client",
u='height:',
v='style="',
w='top:0;',
x='background-repeat:no-repeat;',
y='right:',
z='<div style="',
A='" title="',
B="treevirtual-end",
C="treevirtual-cross",
D=');',
E='<span',
F="treevirtual-line",
G="treevirtual-end-contract",
H=';width:',
I=';"',
J="treevirtual-end-expand",
K="BLANK",
L='>',
M="__lR",
N='bottom:',
O='top:',
P="content-box",
Q="treevirtual-only-contract",
R="treevirtual-start-expand",
S="org.argeo.slc.web.util.TreeDataCellRenderer",
T="mshtml",
U="treevirtual-cross-contract",
V="treevirtual-folder",
W="treevirtual-contract",
X='width:',
Y="__lT",
ba="treevirtual-expand",
bb="treevirtual-cross-expand",
bc=';height:';
qx.Class.define(S,
{extend:qx.ui.treevirtual.SimpleTreeDataCellRenderer,
construct:function(){arguments.callee.base.call(this);
this.__lR=qx.util.AliasManager.getInstance();
this.__lS=qx.util.ResourceManager;
this.__lT=qx.theme.manager.Appearance.getInstance();
this.BLANK=this.__lS.toUri(this.__lR.resolve("static/blank.gif"));
},
statics:{__lU:{}},
members:{_getCellStyle:function(bd){var be=bd.value;
var bf=arguments.callee.base.call(this,
bd)+(be.cellStyle?be.cellStyle+k:b);
return bf;
},
_getContentHtml:function(bd){var bf=b;
var bg=0;
var bh=this._addExtraContentBeforeIndentation(bd,
bg);
bf+=bh.html;
bg=bh.pos;
var bi=this._addIndentation(bd,
bg);
bf+=bi.html;
bg=bi.pos;
bh=this._addExtraContentBeforeIcon(bd,
bg);
bf+=bh.html;
bg=bh.pos;
var bj=this._addIcon(bd,
bg);
bf+=bj.html;
bg=bj.pos;
bh=this._addExtraContentBeforeLabel(bd,
bg);
bf+=bh.html;
bg=bh.pos;
bf+=this._addLabel(bd,
bg);
return bf;
},
_addImage:function(bk){var bf=[];
var bl=this.__lS.toUri(this.__lR.resolve(bk.url));
if(bk.position){var bg=bk.position;
bf.push(g);
if(!qx.core.Variant.isSet(t,
T)){bf.push(qx.bom.element.BoxSizing.compile(P));
}
if(bg.top!==undefined){bf.push(O+bg.top+a);
}
if(bg.right!==undefined){bf.push(y+bg.right+a);
}
if(bg.bottom!==undefined){bf.push(N+bg.bottom+a);
}
if(bg.left!==undefined){bf.push(j+bg.left+a);
}
if(bg.width!==undefined){bf.push(X+bg.width+a);
}
if(bg.height!==undefined){bf.push(u+bg.height+a);
}bf.push(h);
}bf.push(z);
bf.push(m+bl+D);
bf.push(x);
if(bk.imageWidth&&bk.imageHeight){bf.push(H+bk.imageWidth+d+bc+bk.imageHeight+d);
}var bm=bk.tooltip;
if(bm!=null){bf.push(A+bm);
}bf.push(q);
if(bk.position){bf.push(e);
}return bf.join(b);
},
_addIndentation:function(bd,
bg){var be=bd.value;
var bn;
var bf=b;
var bo=this.getUseTreeLines();
var bp=this.getExcludeFirstLevelTreeLines();
var bq=this.getAlwaysShowOpenCloseSymbol();
for(var br=0;br<be.level;br++){bn=this._getIndentSymbol(br,
be,
bo,
bq,
bp);
bf+=this._addImage({url:bn.icon,
position:{top:0+(bn.paddingTop||5),
left:bg+(bn.paddingLeft||3),
width:19,
height:16}});
bg+=19;
}return ({html:bf,
pos:bg});
},
_addIcon:function(bd,
bg){var be=bd.value;
var bs=(be.bSelected?be.iconSelected:be.icon);
if(!bs){if(be.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF){var bt=this.__lT.styleFrom(s);
}else{var bu={opened:be.bOpened};
var bt=this.__lT.styleFrom(V,
bu);
}bs=bt.icon;
}var bf=this._addImage({url:bs,
position:{top:0,
left:bg,
width:19,
height:16}});
return ({html:bf,
pos:bg+19});
},
_addLabel:function(bd,
bg){var be=bd.value;
var bf=g+j+bg+a+w+(be.labelStyle?be.labelStyle+k:b)+h+E+(bd.labelSpanStyle?v+bd.labelSpanStyle+I:b)+L+be.label+l+e;
return bf;
},
_addExtraContentBeforeIndentation:function(bd,
bg){return {html:c,
pos:bg};
},
_addExtraContentBeforeIcon:function(bd,
bg){return {html:c,
pos:bg};
},
_addExtraContentBeforeLabel:function(bd,
bg){return {html:c,
pos:bg};
},
_getIndentSymbol:function(bv,
be,
bo,
bq,
bp){var bw=org.argeo.slc.web.util.TreeDataCellRenderer;
if(bv==0&&bp){bo=false;
}if(bv<be.level-1){return (bo&&!be.lastChild[bv]?bw.__lU.line:{icon:this.BLANK});
}var bx=be.lastChild[be.lastChild.length-1];
if(be.type==qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH&&!be.bHideOpenClose){if(be.children.length>0||bq){if(!bo){return (be.bOpened?bw.__lU.contract:bw.__lU.expand);
}if(bv==0&&be.bFirstChild){if(bx){return (be.bOpened?bw.__lU.onlyContract:bw.__lU.onlyExpand);
}else{return (be.bOpened?bw.__lU.startContract:bw.__lU.startExpand);
}}if(bx){return (be.bOpened?bw.__lU.endContract:bw.__lU.endExpand);
}return (be.bOpened?bw.__lU.crossContract:bw.__lU.crossExpand);
}}if(bo){if(be.parentNodeId==0){if(bx&&be.bFirstChild){return {icon:this.BLANK};
}if(bx){return bw.__lU.end;
}if(be.bFirstChild){return bw.__lU.start;
}}return (bx?bw.__lU.end:bw.__lU.cross);
}return {icon:this.BLANK};
}},
defer:function(){qx.theme.manager.Meta.getInstance().initialize();
var bw=org.argeo.slc.web.util.TreeDataCellRenderer;
var by=qx.io2.ImageLoader;
var bz=qx.util.AliasManager.getInstance();
var bA=qx.util.ResourceManager;
var bB=qx.theme.manager.Appearance.getInstance();
var bC=function(bD){by.load(bA.toUri(bz.resolve(bD)));
};
bw.__lU.line=bB.styleFrom(F);
bC(bw.__lU.line.icon);
bw.__lU.contract=bB.styleFrom(W);
bC(bw.__lU.contract.icon);
bw.__lU.expand=bB.styleFrom(ba);
bC(bw.__lU.expand.icon);
bw.__lU.onlyContract=bB.styleFrom(Q);
bC(bw.__lU.onlyContract.icon);
bw.__lU.onlyExpand=bB.styleFrom(n);
bC(bw.__lU.onlyExpand.icon);
bw.__lU.startContract=bB.styleFrom(p);
bC(bw.__lU.startContract.icon);
bw.__lU.startExpand=bB.styleFrom(R);
bC(bw.__lU.startExpand.icon);
bw.__lU.endContract=bB.styleFrom(G);
bC(bw.__lU.endContract.icon);
bw.__lU.endExpand=bB.styleFrom(J);
bC(bw.__lU.endExpand.icon);
bw.__lU.crossContract=bB.styleFrom(U);
bC(bw.__lU.crossContract.icon);
bw.__lU.crossExpand=bB.styleFrom(bb);
bC(bw.__lU.crossExpand.icon);
bw.__lU.end=bB.styleFrom(B);
bC(bw.__lU.end.icon);
bw.__lU.cross=bB.styleFrom(C);
bC(bw.__lU.cross.icon);
},
destruct:function(){this._disposeFields(M,
r,
Y,
K);
}});
})();
(function(){var a="qooxdoo-table-cell",
b="qx.ui.table.cellrenderer.Html",
c="";
qx.Class.define(b,
{extend:qx.ui.table.cellrenderer.Conditional,
members:{_getContentHtml:function(d){return (d.value||c);
},
_getCellClass:function(d){return a;
}}});
})();
(function(){var a="auto",
b="overflowX",
c="visible",
d="hidden",
e="scroll",
f="overflowY",
g="_applyOverflowX",
h="_applyOverflowY",
i="qx.ui.core.MNativeOverflow";
qx.Mixin.define(i,
{properties:{overflowX:{check:[d,
c,
e,
a],
nullable:true,
apply:g},
overflowY:{check:[d,
c,
e,
a],
nullable:true,
apply:h},
overflow:{group:[b,
f]}},
members:{_applyOverflowX:function(j){this.getContentElement().setStyle(b,
j);
},
_applyOverflowY:function(j){this.getContentElement().setStyle(f,
j);
}}});
})();
(function(){var a="",
b="color",
c="String",
d="none",
e="padding",
f="0px",
g="changeHtml",
h="_applyCssClass",
i="class",
j="qx.ui.embed.Html",
k="_applyHtml",
l="border",
m="html";
qx.Class.define(j,
{extend:qx.ui.core.Widget,
include:[qx.ui.core.MNativeOverflow],
construct:function(n){arguments.callee.base.call(this);
if(n!=null){this.setHtml(n);
}},
properties:{html:{check:c,
apply:k,
event:g,
nullable:true},
cssClass:{check:c,
init:a,
apply:h},
selectable:{refine:true,
init:true},
focusable:{refine:true,
init:true}},
members:{getFocusElement:function(){return this.getContentElement();
},
_applyHtml:function(o,
p){var q=this.getContentElement();
q.setAttribute(m,
o||a);
q.setStyle(e,
f);
q.setStyle(l,
d);
},
_applyCssClass:function(o,
p){this.getContentElement().setAttribute(i,
o);
},
_applyFont:function(o,
p){var r=o?qx.theme.manager.Font.getInstance().resolve(o).getStyles():qx.bom.Font.getDefaultStyles();
this.getContentElement().setStyles(r);
},
_applyTextColor:function(o,
p){if(o){this.getContentElement().setStyle(b,
qx.theme.manager.Color.getInstance().resolve(o));
}else{this.getContentElement().removeStyle(b);
}}}});
})();
