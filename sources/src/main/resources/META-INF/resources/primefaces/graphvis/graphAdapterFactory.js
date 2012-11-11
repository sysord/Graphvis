
/**
 * GraphAdapter factory:
 * provide GraphAdapter implementation 
 */
PrimeFaces.widget.GraphAdapterFactory = function() {	
};

PrimeFaces.widget.GraphAdapterFactory.prototype = { 
	createGraphAdapter : function(graphRenderer, containerId, graphModel, visualStyle, onVisReadyListener, 
									afterLayoutListener, additionalParameters){
		
		//CytoscapeWeb  (flash version)
		if(graphRenderer == 'CSW'){
			return this._createCswGraphAdapter(containerId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, additionalParameters)
		}

		//CytoscapeWeb 2 (html5 version)
		if(graphRenderer == 'CSW2'){
			return this._createCsw2GraphAdapter(containerId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, additionalParameters)
		}

	},

	/**
	 * 	create graph adapter for CytoscapeWeb  (flash version)
	 */
	_createCswGraphAdapter: function(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, additionalParameters){		
		return new PrimeFaces.widget.CswGraphAdapter(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, additionalParameters.cytoscapeWebPath, additionalParameters.flashInstallerPath);
	},
	
	/**
	 * 	create graph adapter for CytoscapeWeb 2 (html5 version)
	 */
	_createCsw2GraphAdapter: function(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener, additionalParameters){
		return new PrimeFaces.widget.Csw2GraphAdapter(divId, graphModel, visualStyle, onVisReadyListener, afterLayoutListener);
	},

};
