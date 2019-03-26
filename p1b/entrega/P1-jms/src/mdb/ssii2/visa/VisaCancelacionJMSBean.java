/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = "update tarjeta set saldo = saldo + pago.importe from pago " +
  "where pago.numerotarjeta=tarjeta.numerotarjeta and idautorizacion=? and codrespuesta = \'000\';"+
  "UPDATE pago " +
    "set codrespuesta = \'999\' where idautorizacion = ? and codrespuesta = \'000\'; ";


  public VisaCancelacionJMSBean() {

  }

  public void cancelarPago(int id) {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    Connection con=null;
    try {
        con = getConnection();
        pstmt = con.prepareStatement(UPDATE_CANCELA_QRY);
        pstmt.setInt(1, id);
        pstmt.setInt(2, id);
        rs = pstmt.executeQuery();
        if (!rs.next()){
            logger.warning("The cancelation query did not work correctly");
        }
    } catch (Exception e) {
        e.printStackTrace();
        logger.warning(e.toString());
    } finally {
        try {
            if (rs != null) {
                rs.close(); rs = null;
            }
            if (pstmt != null) {
                pstmt.close(); pstmt = null;
            }
            if (con != null) {
                closeConnection(con);
                con = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warning(e.toString());
        }
    }
  }
  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      try {
          if (inMessage instanceof TextMessage) {
              msg = (TextMessage) inMessage;
              logger.info("MESSAGE BEAN: Message received: " + msg.getText());
              cancelarPago(Integer.valueOf(msg.getText()));
          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }
  }


}
