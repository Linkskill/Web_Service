require 'tty-prompt'
require_relative 'ticket_menu.rb'
require_relative 'accommodation_menu.rb'
require_relative 'package_menu.rb'
require_relative '../modules/web_service.rb'

# Menu do cliente, o qual pode realizar compras e pesquisas
class Menu
    def initialize
        @prompt = TTY::Prompt.new
    end

    # Exibe a tela de menu e as opções
    def display_menu
        while true
            @prompt.say("\n\t→Menu do Cliente←")
            chosen_option = @prompt.select('Escolha uma opção:', echo: false) do |menu|
                menu.enum '.'
                menu.choice 'Passagens aéreas', :display_ticket_menu
                menu.choice 'Vagas de hospedagem', :display_accomodation_menu
                menu.choice 'Pacotes de viagem', :display_package_menu
                menu.choice 'Verificar conexão com o servidor', :test_service
                menu.choice 'Voltar', :back
            end

            case chosen_option
            when :display_ticket_menu
                ticket_menu = TicketMenu.new
                ticket_menu.display_menu
            when :display_accomodation_menu
                accommodation_menu = AccommodationMenu.new
                accommodation_menu.display_menu
            when :display_package_menu
                package_menu = PackageMenu.new
                package_menu.display_menu
            when :test_service
                test_server
            when :back
                return
            end
        end
    end

    private
        # Contacta o servidor a fim de receber respostas, se o servidor não 
        # responder, então não está funcionando
        def test_server
            if WebService.test
                @prompt.ok("O servidor está conectado")
            else
                @prompt.error("O servidor não está respondendo")
            end
        end

end