package com.clicktable.relationshipModel;

import org.springframework.data.neo4j.annotation.EndNode;
import org.springframework.data.neo4j.annotation.Fetch;
import org.springframework.data.neo4j.annotation.RelationshipEntity;
import org.springframework.data.neo4j.annotation.StartNode;

import com.clicktable.model.Server;
import com.clicktable.model.Table;
import com.clicktable.util.RelationshipTypes;
import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * 
 * @author p.singh
 *
 */
@RelationshipEntity(type = RelationshipTypes.TBL_HAS_SERVER)
public class TableHasServer  extends RelEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -258098319581699051L;
	@Fetch
	@StartNode
	Table table;
	@Fetch
	@EndNode
	@JsonBackReference(value="server")
	Server server;
	
	
	 
	 
	
	public TableHasServer() 
	{
	    super();
	}
	
	public TableHasServer(Table table,Server server)
	{
	    super();
	    this.table = table;
	    this.server = server;
	}


	public Table getTable() {
	    return table;
	}

	public void setTable(Table table) {
	    this.table = table;
	}

	public Server getServer() {
	    return server;
	}

	public void setServer(Server server) {
	    this.server = server;
	}
	


}
