import {SET_PATH} from '@/store/shared/mutationTypes'
import PathService from '@/api/modules/path'

const state = {
  pathResult: null
}

const getters = {
  pathResult(state) {
    return state.pathResult
  }
}

const mutations = {
  [SET_PATH](state, pathResult) {
    state.pathResult = pathResult
  }
}

const actions = {
  async searchPath({ commit }, {}) {
    return PathService.get().then(({ data }) => {
      commit(SET_PATH, data)
    })
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
