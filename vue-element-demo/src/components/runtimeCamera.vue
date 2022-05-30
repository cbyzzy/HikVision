<template>
  <div class="runtimeCamera-main">
    <div class="runtimeCamera-header">{{$t('monitor.runtimeCamera')}}</div>
    <div class="runtimeCamera-body">
      <div class="runtimeCamera-top-container">
        <el-button type="primary" class="runtimeCamera-back-button" size="small" @click="$router.go(-1)">{{ $t('back') }}</el-button>
        <div class="runtimeCamera-top-group">
          <el-button-group class="player-btn-group">
            <el-button type="primary" plain size="medium" v-for="list in playerBtnGroup" :key="list.num" @click.prevent="setPlayerLength(list.num)" :class="{'active' : playerLength === list.num}">{{list.name}}</el-button>
            <el-button type="primary" plain size="medium" v-if="!isMobile()" id="full-btn-medium" @click.prevent="toggleFullScreen()" title="全屏显示"><i class="fa fa-arrows-alt"></i></el-button>
          </el-button-group>
        </div>
      </div>
      <div class="runtimeCamera-middle-container">
        <div class="runtimeCamera-tree">
          <el-button type="primary" plain size="medium" @click.prevent="resetNode()" title="重置节点"><i class="fa fa-refresh"></i></el-button>
          <el-tree class="tree-body" :data="cameraTree" :props="defaultProps" default-expand-all node-key="id" ref="tree" @node-click="handleTreeClick" highlight-current></el-tree>
        </div>
        <div class="runtimeCamera-fullscreen">
          <fullscreen :fullscreen.sync="fullscreen" class="runtimeCamera-video">
            <div class="grid-group">
              <el-row v-for="indexRow of rowLength" :key="indexRow" :gutter="9/rowLength" type="flex" class="row-bg" justify="center">
                <el-col v-for="indexCol of colLength" :key="indexCol" :span="24/colLength">
                  <div class="grid-title">
                    {{arrCamera(indexRow,indexCol).name}}
                  </div>
                  <div :class="(indexRow === selectRow && indexCol === selectCol ? 'grid-content-click' : 'grid-content')" @click="clickPlayer(indexRow, indexCol, $event)" @touchend="clickPlayer(indexRow, indexCol, $event)">
                    <LivePlayer ref="player" :key="divKey[((parseInt(indexRow)) - 1) * rowLength + parseInt(indexCol)]" :videoUrl="arrCamera(indexRow,indexCol).url" :live="arrCamera(indexRow,indexCol).live"
                                :muted="true" :hide-fluent-button="true" @message="$message" :loading.sync="bLoading" v-loading="bLoading" element-loading-text="加载中">
                    </LivePlayer>
                  </div>
                </el-col>
              </el-row>
            </div>
          </fullscreen>
        </div>
        <div class="runtimeCamera-item">
          <div class="modal-item">
            {{ $t('monitor.ptzControl') }}
          </div>
          <div class="ptz-block">
            <div class="ptz-cell ptz-up" command="up" title="上" @mousedown.prevent="ptzControl('TILT_UP', $event)" @mouseup.prevent="ptzStop('TILT_UP', $event)">
              <i class="fa fa-chevron-up"></i>
            </div>
            <div class="ptz-cell ptz-left" command="left" title="左" @mousedown.prevent="ptzControl('PAN_LEFT', $event)" @mouseup.prevent="ptzStop('PAN_LEFT', $event)">
              <i class="fa fa-chevron-left"></i>
            </div>
            <div class="ptz-cell ptz-center" title="云台控制"></div>
            <div class="ptz-cell ptz-right" command="right" title="右" @mousedown.prevent="ptzControl('PAN_RIGHT', $event)" @mouseup.prevent="ptzStop('PAN_RIGHT', $event)">
              <i class="fa fa-chevron-right"></i>
            </div>
            <div class="ptz-cell ptz-down" command="down" title="下" @mousedown.prevent="ptzControl('TILT_DOWN', $event)" @mouseup.prevent="ptzStop('TILT_DOWN', $event)">
              <i class="fa fa-chevron-down"></i>
            </div>
            <div class="ptz-cell ptz-zoomin" command="zoomin" title="放大" @mousedown.prevent="ptzControl('ZOOM_IN', $event)" @mouseup.prevent="ptzStop('ZOOM_IN', $event)">
              <i class="fa fa-plus"></i>
            </div>
            <div class="ptz-cell ptz-zoomout" command="zoomout" title="缩小" @mousedown.prevent="ptzControl('ZOOM_OUT', $event)" @mouseup.prevent="ptzStop('ZOOM_OUT', $event)">
              <i class="fa fa-minus"></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import LivePlayer from "@liveqing/liveplayer"
  import fullscreen from "vue-fullscreen"
  import Vue from "vue"

  Vue.use(fullscreen)

  Vue.prototype.isMobile = () => {
    //videojs是liveplayer下面的文件
    return videojs.browser.IS_IOS || videojs.browser.IS_ANDROID;
  }
  Vue.prototype.isIE = () => {
    return !!videojs.browser.IE_VERSION;
  }

  export default {
    name: 'runtimeCamera',
    data() {
      return {
        testerId: "",
        cameraTree: [],
        cameraArray: [],
        rowLength: 2,
        colLength: 2,
        playerLength: 4,
        fullscreen: false,
        defaultProps: {
          children: 'children',
          label: 'label'
        },
        videoUrl: "",
        protocol: "FLV",
        streamInfo: null,
        bRecording: false,
        bLoading: false,
        ptzCommand: null,
        selectCamera: null,
        selectRow: 0,
        selectCol: 0,
        notificationTime: null,
        divKey: []
      }
    },
    beforeDestroy() {
      for (let i=1;i<=3;i++) {
        for (let j=1;j<=3;j++) {
          this.stopTranscode(i,j)
        }
      }
      clearInterval(this.notificationTime)
    },
    created() {
      this.testerId = this.$route.query.id
      this.initDivKey()
      this.getCameraTree()
      this.initCameraArray()
      this.notification()
    },
    computed: {
      playerBtnGroup() {
        var list = [{
          num: 1,
          name: "单屏"
        }, {
          num: 4,
          name: "四分屏"
        }, {
          num: 9,
          name: "九分屏"
        }];
        return list;
      }
    },
    components: { LivePlayer },
    methods: {
      initDivKey() {
        let _this = this
        for (let i=1;i<=9;i++) {
          _this.divKey[i] = ''
        }
      },
      setPlayerLength(len) {
        let _this = this
        for (let i=1;i<=3;i++) {
          for (let j=1;j<=3;j++) {
            _this.stopTranscode(i,j)
          }
        }
        _this.selectRow = 0
        _this.selectCol = 0
        if (len === 1) {
          _this.rowLength = 1
          _this.colLength = 1
        } else if (len === 4) {
          _this.rowLength = 2
          _this.colLength = 2
        } else if (len === 9) {
          _this.rowLength = 3
          _this.colLength = 3
        }
        _this.playerLength = len
      },
      toggleFullScreen() {
        let _this = this
        if (_this.isMobile() || _this.isIE()) {
          _this.$message({
            type: "error",
            message: "请在电脑或非IE浏览器上使用该功能"
          });
          return;
        }
        _this.fullscreen = !_this.fullscreen
      },
      getCameraTree() {
        let _this = this
        const list = [_this.testerId]
        _this.$http.post('/camera/dev/getCameraTree', list).then(function (resp) {
          let _data = resp.data;
          if (_data.code === 200) {
            let tree = []
            _data.data.forEach(node => {
              if (node.cameraList !== null) {
                let vo = {}
                let vovo = []
                vo.label = node.nvr.name
                vo.id = node.nvr.id
                node.cameraList.forEach(camera => {
                  if (camera.enable === 1) {
                    let dev = {}
                    dev.label = camera.name
                    dev.id = camera.id
                    dev.ptz = camera.ptzControl
                    vovo.push(dev)
                  }
                })
                if (vovo.length > 0) {
                  vo.children = vovo
                  tree.push(vo)
                }
              } else if (node.nvr.type === 0) {
                let vo = {}
                vo.label = node.nvr.name
                vo.id = node.nvr.id
                vo.ptz = node.nvr.ptzControl
                tree.push(vo)
              }
            })
            _this.cameraTree = tree
          } else {
            _this.$message.error(_data.message)
          }
        });
      },
      initCameraArray() {
        let _this = this
        for (var i = 1;i <= 9;i++) {
          let obj = {}
          obj.name = '通道' + i
          obj.id = 0
          obj.ptz = 0
          obj.url = null
          obj.tasker = null
          obj.live = false
          _this.cameraArray[i] = obj
        }
      },
      handleTreeClick(data) {
        let _this = this
        if (data.children === undefined) {
          _this.selectCamera = _this.$refs.tree.getCurrentNode()
        } else {
          _this.selectCamera = null
        }
      },
      clickPlayer(iRow,iCol,event) {
        let _this = this
        _this.selectRow = iRow
        _this.selectCol = iCol
        if (_this.selectCamera !== null) {
          if (_this.arrCamera(iRow,iCol).live === true) {
            if (_this.arrCamera(iRow,iCol).id === _this.selectCamera.id) {
              return
            } else {
              _this.stopTranscode(iRow,iCol)
            }
          }
          _this.arrCamera(iRow,iCol).name = _this.selectCamera.label
          _this.arrCamera(iRow,iCol).id = _this.selectCamera.id
          _this.arrCamera(iRow,iCol).ptz = _this.selectCamera.ptz
          _this.startTranscode(iRow,iCol)
          _this.resetNode()
        }
      },
      startTranscode(row,col) {
        let _this = this
        _this.$http.get('/camera/hikcamera/startTranscode', {
          params: {
            id:  _this.arrCamera(row,col).id,
            ipc: null
          }
        }).then(resp => {
          let _data = resp.data
          if (_data.code === 200) {
            if (!_this.$Util.isEmpty(_data.data.msg)) {
              _this.$message(_data.data.msg)
              if (!_this.$Util.isEmpty(_data.data.flvUrl)) {
                _this.arrCamera(row,col).tasker = _data.data.tasker
                _this.arrCamera(row,col).url = _data.data.flvUrl
                _this.arrCamera(row,col).live = true
                Vue.set(_this.divKey,(row - 1) * _this.rowLength + col,new Date().getTime())
              }
            }
          } else {
            _this.$message.error(_data.message)
          }
        })
      },
      stopTranscode(row,col) {
        let _this = this
        if (_this.arrCamera(row,col).live) {
          _this.$http.get('/camera/flow/stopByTasker', {
            params: {
              tasker: _this.arrCamera(row,col).tasker,
            }
          })
        }
        _this.resetCamera(row,col)
      },
      ptzControl(cmd, event) {
        let _this = this
        if (_this.selectRow === 0 || _this.selectCol === 0) { return }
        if (_this.arrCamera(_this.selectRow,_this.selectCol).ptz === 0) { return }
        _this.startPtzTime = new Date().getTime()
        _this.ptzCommand = cmd
      },
      ptzStop(cmd, event) {
        let _this = this
        let duration = -1
        if (_this.selectRow === 0 || _this.selectCol === 0) { return }
        if (_this.arrCamera(_this.selectRow,_this.selectCol).ptz === 0) { return }
        if (_this.ptzCommand === cmd) {
          if (_this.startPtzTime > 0) {
            duration = new Date().getTime() - _this.startPtzTime
          }
          _this.$http.get('/camera/hikcamera/playControl', {
            params: {
              id: _this.arrCamera(_this.selectRow,_this.selectCol).id,
              ipc: null,
              mill: duration,
              command: _this.ptzCommand
            }
          }).then(resp => {
            let _data = resp.data
            if (_data.code === 200) {
              if (!_this.$Util.isEmpty(_data.data.msg)) {
                if (_data.data.msg === "Async") {
                  _this.$message(_this.$t('monitor.startAsync'))
                } else {
                  _this.$message(_data.data.msg)
                }
              }
            } else {
              _this.$message.error(_data.message)
            }
          })
        }
        _this.ptzCommand = null
        _this.startPtzTime = 0
      },
      notification() {
        let _this = this
        // 定时刷新视频流状态
        _this.notificationTime = setInterval(function () {
          let _list = []
          _this.cameraArray.forEach(function (v) {
            if (v.tasker !== null) {
              _list.push(v.tasker)
            }
          })
          _this.$http.post('/camera/flow/multi-notification', _list)
        },30000);
      },
      arrCamera(row,col) {
        return this.cameraArray[((parseInt(row)) - 1) * this.rowLength + parseInt(col)]
      },
      resetCamera(row,col) {
        let index = ((parseInt(row)) - 1) * this.rowLength + parseInt(col)
        let obj = {}
        obj.name = '通道' + index
        obj.id = 0
        obj.ptz = 0
        obj.url = null
        obj.tasker = null
        obj.live = false
        this.cameraArray[index] = obj
      },
      resetNode() {
        let _this = this
        _this.$refs.tree.setCurrentKey(null)
        _this.selectCamera = null
      }
    }
  }
</script>

<style>
  .runtimeCamera-main {
    width: 100%;
    height: 100%;
    background: white;
    display: flex;
    flex-direction: column;
  }
  .runtimeCamera-header {
    width: calc(100% - 25px);
    height: 60px;
    color: #222;
    font-weight: bold;
    font-size: 15px;
    line-height: 60px;
    padding-left: 25px;
  }
  .runtimeCamera-body {
    width: 100%;
    height: calc(100% - 32px);
    background: white;
    box-sizing: border-box;
    padding: 0px 25px;
    display: flex;
    flex-direction: column;
    align-items: center;
    border-top: 1px solid #e2e8ef;
  }
  .runtimeCamera-top-container {
    width: 100%;
    padding: 0 25px;
    height: 70px;
    background: rgba(226, 232, 239, 0.3);
    border-top: 1px solid #e2e8ef;
    border-bottom: 1px solid #e2e8ef;
    display: flex;
    align-items: center;
  }
  .runtimeCamera-back-button {
    background-color: #0179ff;
    width: 80px;
  }
  .runtimeCamera-back-button:hover {
    background-color: rgba(1, 121, 255, 0.8);
  }
  .runtimeCamera-top-group {
    width: 100%;
    padding: 10px 25px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .player-btn-group {
    margin: 5px 0;
    position:  relative;
    left: -80px;
  }
  .runtimeCamera-middle-container {
    width: 100%;
    height: 100%;
    padding: 0 25px;
    border-top: 1px solid #e2e8ef;
    border-bottom: 1px solid #e2e8ef;
    display: flex;
    align-items: center;
    flex-direction: row;
  }
  .runtimeCamera-tree {
    width: 15%;
    height: calc(100% - 25px);
    align-items: center;
  }
  .tree-body {
    margin-top: 10px;
  }
  .runtimeCamera-fullscreen {
    margin-top: 25px;
    margin-bottom: 25px;
    width: 70%;
    height: calc(100% - 25px);
    align-items: center;
    justify-content: center;
  }
  .runtimeCamera-video {
    color: #e14f4f;
    display: flex;
    align-items: center;
    flex-direction: column;
  }
  .row-bg {

  }
  .grid-group {
    width: 100%;
    height: 100%;
  }
  .grid-title {
    min-height: 18px;
  }
  .grid-content {
    border-top: 4px solid white;
    border-radius: 4px;
    min-height: 150px;
    min-width: 100px;
  }
  .grid-content:hover {
    border-top: 4px solid orange;
    border-radius: 4px;
    min-height: 150px;
    min-width: 100px;
  }
  .grid-content-click {
    border-top: 4px solid orangered;
    border-radius: 4px;
    min-height: 150px;
    min-width: 100px;
  }
  .runtimeCamera-item {
    width: 15%;
    height: 100%;
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .modal-item {
    width: 300px;
    margin: 10px;
    text-align: center;
    font-size: 20px;
    color: #333;
  }
  .ptz-block {
    width: 300px;
    height: 180px;
    margin: 10px;
    text-align: center;
    position: relative;
    font-size: 24px;
    color: #333;
  }
  .ptz-cell {
    width: 50px;
    height: 50px;
    line-height: 50px;
    position: absolute;
    transform:translate(150%,0%);
    cursor: pointer;
  }
  .ptz-cell:active {
    color: #ccc;
    font-size: 26px;
  }
  .ptz-center {
    top: 50px;
    left: 50px;
    border-radius: 25px;
    background-color: #ccc;
  }
  .ptz-up {
    top: 0;
    left: 50px;
  }
  .ptz-left {
    top: 50px;
    left: 0;
  }
  .ptz-right {
    top: 50px;
    left: 100px;
  }
  .ptz-down {
    top: 100px;
    left: 50px;
  }
  .ptz-zoomin {
    top: 150px;
    left: 20px;
  }
  .ptz-zoomout {
    top: 150px;
    left: 80px;
  }
</style>
